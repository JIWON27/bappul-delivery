package com.bappul.delivery.user.web.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressRequest {
  String alias;
  String roadAddress;
  String detailAddress;
  String zipcode;
  Boolean isDefault;
}
