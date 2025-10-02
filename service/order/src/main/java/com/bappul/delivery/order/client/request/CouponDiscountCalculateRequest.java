package com.bappul.delivery.order.client.request;

import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CouponDiscountCalculateRequest {
  Long couponId;
  Long userId;
  Long storeId;
  BigDecimal price; // 배달비 제외한 순수 메뉴 가격
}
