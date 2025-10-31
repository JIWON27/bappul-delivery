package com.bappul.web.v1.response;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum AddressType {
  REGION("지명"),
  ROAD("도로명"),
  REGION_ADDR("지번 주소"),
  ROAD_ADDR("도로명 주소")
  ;

  final String title;
}
