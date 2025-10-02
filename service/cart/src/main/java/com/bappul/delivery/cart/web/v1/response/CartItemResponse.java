package com.bappul.delivery.cart.web.v1.response;

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
public class CartItemResponse {
  Long cartItemId;
  Long menuId;
  String menuName;
  List<MenuOptionSummary> options;
  BigDecimal basePrice;
  int quantity;
  BigDecimal lineTotal;

}
