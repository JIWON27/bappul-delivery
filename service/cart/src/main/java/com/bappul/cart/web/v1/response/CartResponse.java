package com.bappul.cart.web.v1.response;

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
public class CartResponse {
  Long cartId;
  Long storeId;
  String storeName;
  List<CartItemResponse> items;
  BigDecimal totalPrice;
}
