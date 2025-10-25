package com.bappul.catalog.web.v1.response.menu.internal;

import java.math.BigDecimal;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public  class CartItemCalculateResponse {
  Long menuId;
  String menuName;
  BigDecimal basePrice;
  List<OptionPrice> optionPrices; // 옵션별 단가 목록
  BigDecimal optionUnitPrice; // 옵션 단가 합
  BigDecimal unitPrice;            // basePrice + optionUnitPrice
  int quantity;
  BigDecimal lineTotalPrice;            // unitPrice * quantity
}
