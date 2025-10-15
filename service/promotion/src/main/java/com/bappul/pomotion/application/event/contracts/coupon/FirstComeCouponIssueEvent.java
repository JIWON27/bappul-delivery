package com.bappul.pomotion.application.event.contracts.coupon;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FirstComeCouponIssueEvent {
  String eventId;
  String eventType;
  Long userId;
  Long couponPolicyId;
}
