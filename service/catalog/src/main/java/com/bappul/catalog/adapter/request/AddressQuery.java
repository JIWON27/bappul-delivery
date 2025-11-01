package com.bappul.catalog.adapter.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressQuery {
  String query;         // 필수
  String analyze_type;  // 선택
  Integer page;         // 선택
  Integer size;         // 선택
}
