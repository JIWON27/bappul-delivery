package com.bappul.pomotion.application.service;

import com.bappul.pomotion.application.mapper.CouponMapper;
import com.bappul.pomotion.application.utils.TimeUtils;
import com.bappul.pomotion.application.validator.CouponValidator;
import com.bappul.pomotion.domain.entity.Coupon;
import com.bappul.pomotion.domain.entity.CouponPolicy;
import com.bappul.pomotion.domain.entity.DiscountType;
import com.bappul.pomotion.domain.repository.CouponPolicyRepository;
import com.bappul.pomotion.domain.repository.CouponRepository;
import com.bappul.pomotion.web.v1.request.CouponCreateRequest;
import com.bappul.pomotion.web.v1.request.CouponPolicyRequest;
import com.bappul.pomotion.web.v1.request.internal.CouponDiscountCalculateRequest;
import com.bappul.pomotion.web.v1.response.CouponPolicyResponse;
import com.bappul.pomotion.web.v1.response.CouponResponse;
import com.bappul.pomotion.web.v1.response.internal.CouponDiscountCalculateResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
  private final TimeUtils timeUtils;
  private final CouponBuilder couponBuilder;
  private final CouponValidator couponValidator;

  @Transactional
  public void createCouponPolicy(CouponPolicyRequest request) {
    CouponPolicy newCouponPolicy = couponMapper.toCouponPolicy(request);
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

    LocalDateTime issuedAt = timeUtils.now();
    coupon.markAsIssued(issuedAt);

    LocalDateTime expiresAt = expirationCalculator.computeExpiresAt(coupon.getCouponPolicy(), issuedAt);
    coupon.updateExpiresAt(expiresAt);
  }

  @Transactional
  public void issueCoupon(Long couponPolicyId, Long userId){
    LocalDateTime issuedAt = timeUtils.now();

    CouponPolicy couponPolicy = couponValidator.getCouponPolicy(couponPolicyId);
    couponValidator.validateCouponIssuable(couponPolicy, userId);

    Coupon coupon = couponBuilder.generateCoupon(couponPolicy, userId);
    coupon.markAsIssued(issuedAt);

    LocalDateTime expiresAt = expirationCalculator.computeExpiresAt(coupon.getCouponPolicy(), issuedAt);
    coupon.updateExpiresAt(expiresAt);

    couponPolicy.incrementIssuedQuantity();
    couponRepository.save(coupon);
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

  @Transactional(readOnly = true)
  public CouponDiscountCalculateResponse calculateDiscount(CouponDiscountCalculateRequest request) {

    Long couponId = request.getCouponId();
    BigDecimal subtotalWithoutDeliveryFee = request.getPrice();

    Coupon coupon = couponValidator.getCoupon(couponId);
    couponValidator.validateUsableForPricing(coupon, request.getUserId(), subtotalWithoutDeliveryFee);

    CouponPolicy couponPolicy = coupon.getCouponPolicy();
    BigDecimal discountPrice = calculateDiscountPriceByDiscountType(couponPolicy, subtotalWithoutDeliveryFee);

    discountPrice = discountPrice
        .min(subtotalWithoutDeliveryFee)
        .setScale(0, RoundingMode.DOWN);

    return CouponDiscountCalculateResponse.builder()
        .couponId(couponId)
        .discount(discountPrice)
        .build();
  }

  private BigDecimal calculateDiscountPriceByDiscountType(CouponPolicy couponPolicy, BigDecimal subtotalWithoutDeliveryFee) {
    final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    BigDecimal discountPrice = BigDecimal.ZERO;
    DiscountType discountType = couponPolicy.getDiscountType();

    switch (discountType) {
      case FIXED -> discountPrice = BigDecimal.valueOf(couponPolicy.getDiscountValue());
      case PERCENTAGE -> {
        int percent = couponPolicy.getDiscountValue();
        BigDecimal raw = subtotalWithoutDeliveryFee
            .multiply(BigDecimal.valueOf(percent))
            .divide(ONE_HUNDRED);
        BigDecimal max = couponPolicy.getMaxDiscountPrice();
        discountPrice = (max != null) ? raw.min(max) : raw;
      }
    }
    return discountPrice;
  }
}
