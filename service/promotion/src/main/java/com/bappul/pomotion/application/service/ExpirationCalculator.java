package com.bappul.pomotion.application.service;

import static com.bappul.pomotion.exception.ServiceExceptionCode.INVALID_EXPIRATION_TYPE;

import com.bappul.pomotion.application.utils.TimeUtils;
import com.bappul.pomotion.domain.entity.CouponPolicy;
import com.bappul.pomotion.domain.entity.ExpirationType;
import exception.ServiceException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExpirationCalculator {

  private final TimeUtils timeUtils;

  public LocalDateTime computeExpiresAt(CouponPolicy policy) {
    switch (policy.getExpirationType()) {
      case COUPON_CREATED -> {
        return timeUtils.now().plusDays(policy.getValidDays());
      }
      case FIXED_PERIOD -> {
        return policy.getEndDate();
      }
      case ISSUE_RELATIVE -> {
        return null;
      }
    }
    throw new ServiceException(INVALID_EXPIRATION_TYPE);
  }

  public LocalDateTime computeExpiresAt(CouponPolicy policy, LocalDateTime issuedAt) {
    if (policy.getExpirationType() != ExpirationType.ISSUE_RELATIVE) {
      return computeExpiresAt(policy);
    }
    return issuedAt.plusDays(policy.getValidDays());
  }
}
