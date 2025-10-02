package com.moduda.delivery.cart.client.response;

import java.math.BigDecimal;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PricingInternalResponse {
  Long storeId;
  String storeName;
  List<CartItemCalculateResponse> items;
  BigDecimal totalPrice;
}
