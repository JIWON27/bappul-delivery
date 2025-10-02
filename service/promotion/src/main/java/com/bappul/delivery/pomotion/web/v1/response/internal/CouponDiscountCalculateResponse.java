package com.bappul.delivery.pomotion.web.v1.response.internal;

import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CouponDiscountCalculateResponse {
  Long couponId;
  BigDecimal discount;
}
