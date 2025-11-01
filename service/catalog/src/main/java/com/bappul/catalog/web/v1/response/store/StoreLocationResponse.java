package com.bappul.catalog.web.v1.response.store;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StoreLocationResponse {
  String roadAddress;
  String detailAddress;
  String zipcode;
  String jibunAddress;
  String haengjeongCode;
  String beopjeongCode;
  Double longitude;
  Double latitude;
}
