package com.bappul.delivery.pomotion.web.v1.request.internal;

import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CouponDiscountCalculateRequest {
  Long couponId;
  Long userId;
  Long storeId;
  BigDecimal price; // 배달비 제외한 순수 메뉴 가격
}
