package com.bappul.delivery.catalog.web.v1.request.menu;

import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MenuRequest {
  String name;
  BigDecimal price;
  String description;
}
