package com.moduda.delivery.pomotion.application.validator;

import static com.moduda.delivery.pomotion.exception.ServiceExceptionCode.ALREADY_ISSUED_COUPON;
import static com.moduda.delivery.pomotion.exception.ServiceExceptionCode.ALREADY_USED_COUPON;
import static com.moduda.delivery.pomotion.exception.ServiceExceptionCode.EXCEEDED_COUPON_ISSUE_LIMIT;
import static com.moduda.delivery.pomotion.exception.ServiceExceptionCode.INVALID_COUPON_CODE;
import static com.moduda.delivery.pomotion.exception.ServiceExceptionCode.NOT_FOUND_COUPON;
import static com.moduda.delivery.pomotion.exception.ServiceExceptionCode.NOT_FOUND_COUPON_POLICY;
import static com.moduda.delivery.pomotion.exception.ServiceExceptionCode.POLICY_INACTIVE;

import com.moduda.delivery.pomotion.domain.entity.Coupon;
import com.moduda.delivery.pomotion.domain.entity.CouponPolicy;
import com.moduda.delivery.pomotion.domain.entity.CouponStatus;
import com.moduda.delivery.pomotion.domain.repository.CouponPolicyRepository;
import com.moduda.delivery.pomotion.domain.repository.CouponRepository;
import exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponValidator {

  private final CouponPolicyRepository couponPolicyRepository;
  private final CouponRepository couponRepository;

  public CouponPolicy getCouponPolicy(Long couponPolicyId){
    return couponPolicyRepository.findById(couponPolicyId)
        .orElseThrow(() -> new ServiceException(NOT_FOUND_COUPON_POLICY));
  }

  public void validateCouponIssuable(CouponPolicy policy, Long userId) {
    // 정책 활성화 여부
    if (!policy.isActive()) {
      throw new ServiceException(POLICY_INACTIVE);
    }

    // 사용자별 발급 한도 체크
    long count = couponRepository.countByUserIdAndCouponPolicyId(userId, policy.getId());
    if (count >= policy.getPerUserLimit()) {
      throw new ServiceException(EXCEEDED_COUPON_ISSUE_LIMIT);
    }

    // 쿠폰 발급 한도 체크
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
}
