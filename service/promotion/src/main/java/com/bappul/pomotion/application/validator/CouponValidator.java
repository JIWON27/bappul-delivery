package com.bappul.pomotion.application.validator;

import static com.bappul.pomotion.exception.ServiceExceptionCode.ALREADY_ISSUED_COUPON;
import static com.bappul.pomotion.exception.ServiceExceptionCode.ALREADY_USED_COUPON;
import static com.bappul.pomotion.exception.ServiceExceptionCode.EXCEEDED_COUPON_ISSUE_LIMIT;
import static com.bappul.pomotion.exception.ServiceExceptionCode.INVALID_COUPON_CODE;
import static com.bappul.pomotion.exception.ServiceExceptionCode.NOT_FOUND_COUPON;
import static com.bappul.pomotion.exception.ServiceExceptionCode.NOT_FOUND_COUPON_POLICY;
import static com.bappul.pomotion.exception.ServiceExceptionCode.POLICY_INACTIVE;

import com.bappul.pomotion.application.service.DDayCalculator;
import com.bappul.pomotion.domain.entity.Coupon;
import com.bappul.pomotion.domain.entity.CouponPolicy;
import com.bappul.pomotion.domain.entity.CouponStatus;
import com.bappul.pomotion.domain.repository.CouponPolicyRepository;
import com.bappul.pomotion.domain.repository.CouponRepository;
import com.bappul.pomotion.exception.ServiceExceptionCode;
import exception.ServiceException;
import java.math.BigDecimal;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponValidator {

  private final CouponPolicyRepository couponPolicyRepository;
  private final CouponRepository couponRepository;
  private final DDayCalculator dayCalculator;

  public CouponPolicy getCouponPolicy(Long couponPolicyId){
    return couponPolicyRepository.findById(couponPolicyId)
        .orElseThrow(() -> new ServiceException(NOT_FOUND_COUPON_POLICY));
  }

  public void validateCouponIssuable(CouponPolicy policy, Long userId) {
    if (!policy.isActive()) {
      throw new ServiceException(POLICY_INACTIVE);
    }

    long count = couponRepository.countByUserIdAndCouponPolicyId(userId, policy.getId());
    if (count >= policy.getPerUserLimit()) {
      throw new ServiceException(EXCEEDED_COUPON_ISSUE_LIMIT);
    }

    if (policy.getTotalQuantity() <= policy.getIssuedQuantity()) {
      throw new ServiceException(EXCEEDED_COUPON_ISSUE_LIMIT);
    }
  }

  public Coupon getCoupon(Long couponId){
    return couponRepository.findById(couponId).orElseThrow(() -> new ServiceException(
        NOT_FOUND_COUPON));
  }

  public Coupon getCoupon(String code){
    Coupon coupon = couponRepository.findByCodeWithLock(code).orElseThrow(
        () -> new ServiceException(INVALID_COUPON_CODE));
    if (coupon.getUserId() != null){
      throw new ServiceException(ALREADY_USED_COUPON);
    }
    return coupon;
  }

  public void validateCouponIssueLimit(int issuedQuantity, int requestQuantity, int totalQuantity) {
    if (issuedQuantity + requestQuantity > totalQuantity) {
      throw new ServiceException(EXCEEDED_COUPON_ISSUE_LIMIT);
    }
  }

  public void validateAlreadyIssued(Long couponPolicyId, Long userId) {
    boolean exists = couponRepository.existsByCouponPolicyIdAndUserIdAndStatus(couponPolicyId, userId, CouponStatus.ISSUED);
    if (exists) {
      throw new ServiceException(ALREADY_ISSUED_COUPON);
    }
  }

  public void validateUsableForPricing(Coupon coupon, Long userId, BigDecimal subtotalWithoutDeliveryFee) {
    assertOwnedBy(coupon, userId);
    assertNotExpired(coupon);
    assertNotUsed(coupon);
    assertMinOrderPrice(coupon.getCouponPolicy(), subtotalWithoutDeliveryFee);
  }

  private void assertOwnedBy(Coupon coupon, Long userId) {
    if (!Objects.equals(coupon.getUserId(), userId)) {
      throw new ServiceException(ServiceExceptionCode.COUPON_NOT_OWNED);
    }
  }

  private void assertNotExpired(Coupon coupon) {
    if (dayCalculator.isExpired(coupon.getExpiresAt())) {
      throw new ServiceException(ServiceExceptionCode.COUPON_EXPIRED);
    }
  }

  private void assertNotUsed(Coupon coupon) {
    if (coupon.getStatus().equals(CouponStatus.USED)) {
      throw new ServiceException(ServiceExceptionCode.COUPON_ALREADY_USED);
    }
  }

  private void assertMinOrderPrice(CouponPolicy policy, BigDecimal subtotalWithoutDeliveryFee) {
    BigDecimal min = policy.getMinOrderPrice();
    if (min != null && subtotalWithoutDeliveryFee.compareTo(min) < 0) {
      throw new ServiceException(ServiceExceptionCode.ORDER_MIN_TOTAL_NOT_MET);
    }
  }
}
