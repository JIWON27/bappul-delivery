package com.bappul.cart.adapter.pricing.response;

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
public class CartItemCalculateResponse {
  Long menuId;
  String menuName;
  BigDecimal basePrice;
  List<OptionPrice> optionPrices;
  BigDecimal optionUnitPrice;
  BigDecimal unitPrice;            // base + optionUnitSum
  Integer quantity;
  BigDecimal lineTotalPrice;            // unitPrice * quantity
}
