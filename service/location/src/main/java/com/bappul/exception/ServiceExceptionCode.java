package com.bappul.exception;

import exception.ServiceErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum ServiceExceptionCode implements ServiceErrorCode {

  ADDRESS_NOT_FOUND("해당 주소에 맞는 주소가 없습니다.", 400),
  NOT_ROAD_ADDR("도로명 주소로 입력해주세요.", 400),

  ;
  final String message;
  final int status;
}
