package com.bappul.catalog.web.v1.response.store;

import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StoreResponse {
  Long storeId;
  String storeName;
  String category;
  String introduction;
  String roadAddress;
  String detailAddress;
  BigDecimal minOrderPrice;
  BigDecimal deliveryFee;
  Boolean openStatus;
}
