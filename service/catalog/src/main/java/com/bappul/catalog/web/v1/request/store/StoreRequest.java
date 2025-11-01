package com.bappul.catalog.web.v1.request.store;

import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StoreRequest {
  Long categoryId;
  Long userId;
  String name;
  String phone;
  String roadAddress;
  String detailAddress;
  String introduction;
  BigDecimal minOrderPrice;
  BigDecimal deliveryFee;
  Boolean openStatus;
}
