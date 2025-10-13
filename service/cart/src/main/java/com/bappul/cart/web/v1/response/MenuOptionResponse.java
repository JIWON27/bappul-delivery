package com.bappul.cart.web.v1.response;

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
public class MenuOptionResponse {
  Long optionItemId;
  String optionName;
  BigDecimal optionAdditionalPrice;
}
