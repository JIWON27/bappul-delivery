package com.moduda.delivery.catalog.web.v1.response.menu.internal;

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
public  class CartItemCalculateResponse {
  Long menuId;
  String menuName;
  BigDecimal basePrice;
  List<OptionPerPrice> optionPerPrices;
  BigDecimal optionUnitPrice;
  BigDecimal unitPrice;            // base + optionUnitSum
  int quantity;
  BigDecimal lineTotal;            // unitPrice * quantity
}
