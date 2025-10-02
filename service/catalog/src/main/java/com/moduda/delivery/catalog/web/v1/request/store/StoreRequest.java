package com.moduda.delivery.catalog.web.v1.request.store;

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
  String name;
  String phoneNumber;
  String address;
  String introduction;
  BigDecimal minOrderPrice;
  BigDecimal deliveryFee;
}
