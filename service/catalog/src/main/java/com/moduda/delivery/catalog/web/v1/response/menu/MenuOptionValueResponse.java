package com.moduda.delivery.catalog.web.v1.response.menu;

import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MenuOptionValueResponse {
  Long id;
  String name;
  BigDecimal additionalPrice;
  Boolean soldOut;
}
