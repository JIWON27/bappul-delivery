package com.bappul.pomotion.application.service;

import com.bappul.pomotion.application.mapper.CouponMapper;
import com.bappul.pomotion.application.validator.CouponValidator;
import com.bappul.pomotion.domain.entity.Coupon;
import com.bappul.pomotion.domain.entity.CouponPolicy;
import com.bappul.pomotion.domain.entity.CouponStatus;
import com.bappul.pomotion.domain.entity.DiscountType;
import com.bappul.pomotion.domain.repository.CouponPolicyRepository;
import com.bappul.pomotion.domain.repository.CouponRepository;
import com.bappul.pomotion.exception.ServiceExceptionCode;
import com.bappul.pomotion.web.v1.request.CouponCreateRequest;
import com.bappul.pomotion.web.v1.request.CouponPolicyRequest;
import com.bappul.pomotion.web.v1.request.internal.CouponDiscountCalculateRequest;
import com.bappul.pomotion.web.v1.response.CouponPolicyResponse;
import com.bappul.pomotion.web.v1.response.CouponResponse;
import com.bappul.pomotion.web.v1.response.internal.CouponDiscountCalculateResponse;
import exception.ServiceException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponService {

  private final CouponPolicyRepository couponPolicyRepository;
  private final CouponRepository couponRepository;

  private final CouponMapper couponMapper;

  private final ExpirationCalculator expirationCalculator;
  private final DDayCalculator dDayCalculator;
  private final CouponBuilder couponBuilder;
  private final CouponValidator couponValidator;

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

    LocalDateTime expiresAt = expirationCalculator.getExpiresAt(coupon.getCouponPolicy());
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

    int dDay = dDayCalculator.calculateDDay(coupon);
    boolean expired = dDayCalculator.isExpired(coupon.getExpiresAt());

    return CouponResponse.of(coupon, couponPolicy, dDay, expired);
  }

  @Transactional(readOnly = true)
  public List<CouponResponse> getCoupons(Long userId) {
    List<Coupon> coupons = couponRepository.findAllByUserId(userId);

    List<CouponResponse> responses = new ArrayList<>();
    for (Coupon coupon : coupons) {
      CouponPolicy couponPolicy = coupon.getCouponPolicy();

      int dDay = dDayCalculator.calculateDDay(coupon);
      boolean expired = dDayCalculator.isExpired(coupon.getExpiresAt());

      responses.add(CouponResponse.of(coupon, couponPolicy, dDay, expired));
    }
    return responses;
  }

  @Transactional(readOnly = true)
  public CouponPolicyResponse getCouponPolicy(Long couponPolicyId) {
    CouponPolicy couponPolicy = couponValidator.getCouponPolicy(couponPolicyId);
    return couponMapper.toResponse(couponPolicy);
  }

  @Transactional
  public CouponDiscountCalculateResponse calculateDiscount(CouponDiscountCalculateRequest request) {
    BigDecimal discountPrice = BigDecimal.ZERO;

    Long couponId = request.getCouponId();
    BigDecimal subtotal = request.getPrice();

    Coupon coupon = couponValidator.getCoupon(couponId);

    ensureUsableForPricing(coupon, request.getUserId());

    CouponPolicy couponPolicy = coupon.getCouponPolicy();

    if (couponPolicy.getMinOrderPrice() != null && subtotal.compareTo(couponPolicy.getMinOrderPrice()) < 0) {
      throw new ServiceException(ServiceExceptionCode.ORDER_MIN_TOTAL_NOT_MET);
    }

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

    discountPrice = discountPrice.min(subtotal);

    return CouponDiscountCalculateResponse.builder()
        .couponId(couponId)
        .discount(discountPrice)
        .build();
  }

  private void ensureUsableForPricing(Coupon coupon, Long userId) {
    if (!Objects.equals(coupon.getUserId(), userId)) {
      throw new ServiceException(ServiceExceptionCode.COUPON_NOT_OWNED);
    }

    if (dDayCalculator.isExpired(coupon.getExpiresAt())) {
      throw new ServiceException(ServiceExceptionCode.COUPON_EXPIRED);
    }

    if (coupon.getStatus().equals(CouponStatus.USED)) {
      throw new ServiceException(ServiceExceptionCode.COUPON_ALREADY_USED);
    }
  }
}
