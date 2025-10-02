package com.bappul.delivery.pomotion.application.event.consumer;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FirstComeCouponIssueEvent {
  Long userId;
  Long couponPolicyId;
}
