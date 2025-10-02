package com.moduda.delivery.catalog.web.v1.response.menu.internal;

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
public class OptionPerPrice {
  Long optionValueId;
  String optionName;
  BigDecimal optionPrice;
}
