package com.moduda.delivery.user.web.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressResponse {
  String alias;
  String zipcode;
  String roadAddress;
  String detailAddress;
  Boolean isDefault;
}
