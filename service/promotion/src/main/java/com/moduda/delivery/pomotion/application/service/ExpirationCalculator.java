package com.moduda.delivery.pomotion.application.service;

import com.moduda.delivery.pomotion.domain.entity.CouponPolicy;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
public class ExpirationCalculator {

  /**
   * [TODO] 시간 만료일 디테일 챙기기
   * 현재는 단순하게 LocalDateTime으로..
   */

  public LocalDateTime getExpiresAt(CouponPolicy policy) {
    switch (policy.getExpirationType()) {
      case COUPON_CREATED -> {
        // 생성일 ~ 생성일 + validDays
        LocalDateTime now =  LocalDateTime.now();
        return now.plusDays(policy.getValidDays());
      }
      case FIXED_PERIOD -> {
        // startDay ~ endDays
        return policy.getEndDate();
      }
      case ISSUE_RELATIVE -> {
        // 발급일 ~ validDays
        return null;
      }
    }
    throw new IllegalArgumentException("Invalid expiration type");
  }

}
