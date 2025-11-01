package com.bappul.delivery.user.adapter.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LocationResponse {
  String roadAddress; // 전체 도로명 주소
  String jibunAddress; // 전체 지번 주소
  String zoneNo; // 우편번호
  Double longitude;
  Double latitude;
  String haengJeongCode;
  String beopJeongCode;
}
