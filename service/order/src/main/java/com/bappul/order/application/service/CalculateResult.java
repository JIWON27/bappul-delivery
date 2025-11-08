package com.bappul.order.application.service;

import com.bappul.order.adapter.response.CartItemCalculateResponse;
import java.math.BigDecimal;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CalculateResult {
  BigDecimal orderSubtotalPrice;
  BigDecimal deliveryFeePrice;
  BigDecimal payableTotalPrice;
  BigDecimal orderDiscountPrice;
  List<CartItemCalculateResponse> cartItemCalculateResponses;
}
