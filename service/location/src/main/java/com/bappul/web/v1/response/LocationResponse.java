package com.bappul.web.v1.response;

import lombok.Builder;

@Builder
public class LocationResponse {
  String roadAddress; // 전체 도로명 주소
  String jibunAddress; // 전체 지번 주소
  String zoneNo; // 우편번호
  Double longitude;
  Double latitude;
  String haengJeongCode;
  String beopJeongCode;
}
