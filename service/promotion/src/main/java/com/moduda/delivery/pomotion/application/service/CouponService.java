package com.moduda.delivery.pomotion.application.service;

import com.moduda.delivery.pomotion.application.mapper.CouponMapper;
import com.moduda.delivery.pomotion.application.validator.CouponValidator;
import com.moduda.delivery.pomotion.domain.entity.Coupon;
import com.moduda.delivery.pomotion.domain.entity.CouponPolicy;
import com.moduda.delivery.pomotion.domain.entity.CouponStatus;
import com.moduda.delivery.pomotion.domain.entity.DiscountType;
import com.moduda.delivery.pomotion.domain.repository.CouponPolicyRepository;
import com.moduda.delivery.pomotion.domain.repository.CouponRepository;
import com.moduda.delivery.pomotion.exception.ServiceExceptionCode;
import com.moduda.delivery.pomotion.web.v1.request.CouponCreateRequest;
import com.moduda.delivery.pomotion.web.v1.request.CouponPolicyRequest;
import com.moduda.delivery.pomotion.web.v1.request.internal.CouponDiscountCalculateRequest;
import com.moduda.delivery.pomotion.web.v1.response.CouponPolicyResponse;
import com.moduda.delivery.pomotion.web.v1.response.CouponResponse;
import com.moduda.delivery.pomotion.web.v1.response.internal.CouponDiscountCalculateResponse;
import exception.ServiceException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponService {

  private final CouponRepository couponRepository;
  private final CouponBuilder couponBuilder;
  private final CouponValidator couponValidator;
  private final CouponMapper couponMapper;
  private final CouponPolicyRepository couponPolicyRepository;
  private final ExpirationCalculator expirationCalculator;

  @Transactional
  public void createCouponPolicy(CouponPolicyRequest request) {
    CouponPolicy newCouponPolicy = couponMapper.toEntity(request);
    couponPolicyRepository.save(newCouponPolicy);
  }

  @Transactional
  public void preIssueCoupons(CouponCreateRequest request) {
    CouponPolicy couponPolicy = couponValidator.getCouponPolicy(request.getCouponPolicyId());
    List<Coupon> coupons = couponBuilder.generateCoupons(couponPolicy, request);
    couponRepository.saveAll(coupons);
  }

  @Transactional
  public void registerOfflineCouponCode(String code, Long userId) {
    Coupon coupon = couponValidator.getCoupon(code);
    coupon.updateUser(userId);
    coupon.markAsIssued();

    CouponPolicy couponPolicy = couponValidator.getCouponPolicy(coupon.getCouponPolicy().getId());
    LocalDateTime expiresAt = expirationCalculator.getExpiresAt(couponPolicy);
    coupon.updateExpiresAt(expiresAt);
  }

  @Transactional
  public void issueCoupon(Long couponPolicyId, Long userId){
    CouponPolicy couponPolicy = couponValidator.getCouponPolicy(couponPolicyId);

    couponValidator.validateCouponIssuable(couponPolicy, userId);

    Coupon coupon = couponBuilder.generateCoupon(couponPolicy, userId);
    couponPolicy.incrementIssuedQuantity();
    couponRepository.save(coupon);

    coupon.markAsIssued();
  }

  @Transactional(readOnly = true)
  public CouponResponse getCoupon(Long couponId) {
    Coupon coupon = couponValidator.getCoupon(couponId);
    CouponPolicy couponPolicy = coupon.getCouponPolicy();

    // d-day 계산
    ZoneId zone = ZoneId.of("Asia/Seoul");
    LocalDateTime now = LocalDateTime.now(zone);
    LocalDate today = now.toLocalDate();
    LocalDateTime expiresAt = coupon.getExpiresAt();

    boolean hasExpire = Objects.nonNull(coupon.getExpiresAt());
    boolean expired = hasExpire && now.isAfter(coupon.getExpiresAt());

    int dDay = 0;
    if (hasExpire && !expired)  {
      LocalDate expDate = expiresAt.atZone(zone).toLocalDate();
      dDay = (int) ChronoUnit.DAYS.between(today, expDate);
    }

    return CouponResponse.of(coupon, couponPolicy, dDay, expired);
  }

  @Transactional(readOnly = true)
  public List<CouponResponse> getCoupons(Long userId) {
    List<Coupon> coupons = couponRepository.findAllByUserId(userId);

    // d-day 계산
    ZoneId zone = ZoneId.of("Asia/Seoul");
    LocalDateTime now = LocalDateTime.now(zone);
    LocalDate today = now.toLocalDate();

    List<CouponResponse> responses = new ArrayList<>();
    for (Coupon coupon : coupons) {
      CouponPolicy couponPolicy = coupon.getCouponPolicy();
      boolean expired = (coupon.getExpiresAt() != null) && now.isAfter(coupon.getExpiresAt());

      int dDay = 0;
      if (!expired && coupon.getExpiresAt() != null) {
        LocalDate expDate = coupon.getExpiresAt().atZone(zone).toLocalDate();
        dDay = (int) ChronoUnit.DAYS.between(today, expDate);
      }

      responses.add(CouponResponse.of(coupon, couponPolicy, dDay, expired));
    }

    return responses;
  }

  @Transactional(readOnly = true)
  public CouponPolicyResponse getCouponPolicy(Long couponPolicyId) {
    CouponPolicy couponPolicy = couponValidator.getCouponPolicy(couponPolicyId);
    return couponMapper.toResponse(couponPolicy);
  }

  // 쿠폰 할인 계산 로직
  public CouponDiscountCalculateResponse calculateDiscount(CouponDiscountCalculateRequest request) {
    BigDecimal discountPrice = BigDecimal.ZERO;

    Long couponId = request.getCouponId();
    BigDecimal subtotal = request.getPrice(); // 배송비 제외한 순수 음식 가격

    Coupon coupon = couponValidator.getCoupon(couponId);

    // 쿠폰 사용가능한지 검증
    ensureUsableForPricing(coupon, request.getUserId());

    CouponPolicy couponPolicy = coupon.getCouponPolicy();

    // 최소 주문 금액 미달 시 0원
    if (couponPolicy.getMinOrderPrice() != null && subtotal.compareTo(couponPolicy.getMinOrderPrice()) < 0) {
      throw new ServiceException(ServiceExceptionCode.ORDER_MIN_TOTAL_NOT_MET);
    }

    // 할인 타입 확인
    DiscountType discountType = couponPolicy.getDiscountType();
    switch (discountType) {
      case FIXED -> {
        BigDecimal fixed = BigDecimal.valueOf(couponPolicy.getDiscountValue());
        discountPrice = fixed.min(subtotal).setScale(0, RoundingMode.DOWN);
      }
      case PERCENTAGE -> {
        int percent = couponPolicy.getDiscountValue();
        BigDecimal raw = subtotal.multiply(BigDecimal.valueOf(percent))
            .divide(BigDecimal.valueOf(100), 0, RoundingMode.DOWN);

        BigDecimal max = couponPolicy.getMaxDiscountPrice();
        discountPrice = (max != null) ? raw.min(max) : raw;
      }
    }

    // 최종 상한 결정
    discountPrice = discountPrice.min(subtotal);

    return CouponDiscountCalculateResponse.builder()
        .couponId(couponId)
        .discount(discountPrice)
        .build();
  }

  // 쿠폰 사용 가능성 검증
  private void ensureUsableForPricing(Coupon coupon, Long userId) {
    // 쿠폰 소유자 검증
    if (!Objects.equals(coupon.getUserId(), userId)) {
      throw new ServiceException(ServiceExceptionCode.COUPON_NOT_OWNED);
    }

    // 만료 시간 검증
    if (coupon.getExpiresAt() != null && LocalDateTime.now().isAfter(coupon.getExpiresAt())) {
      throw new ServiceException(ServiceExceptionCode.COUPON_EXPIRED);
    }

    // 쿠폰 사용 검증
    if (coupon.getStatus().equals(CouponStatus.USED)) {
      throw new ServiceException(ServiceExceptionCode.COUPON_ALREADY_USED);
    }

    // TODO 쿠폰 적용 범위 Scope 검증 로직 추가
  }
}
