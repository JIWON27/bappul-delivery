package com.moduda.delivery.pomotion.application.event.contracts.coupon;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CouponEventPayload {
  Long orderId;
  Long userId;
  Long couponId;
}
