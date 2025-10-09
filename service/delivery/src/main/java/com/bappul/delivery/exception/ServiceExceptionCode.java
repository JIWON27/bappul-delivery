package com.bappul.delivery.exception;

import exception.ServiceErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum ServiceExceptionCode implements ServiceErrorCode {
  NOT_FOUND_DELIVERY("존재하지 않는 배달 정보입니다.", 404),

  NOT_FOUND_OUTBOX_EVENT("존재하지 않는 아웃박스 이벤트 입니다.", 500),
  JSON_SERIALIZATION_ERROR("JSON 직렬화에 실패했습니다.", 500),
  JSON_DESERIALIZATION_ERROR("JSON 역직렬화에 실패했습니다.", 400),
  IDEMPOTENCY_KEY("멱등성 검증 오류", 500),

  ;
  final String message;
  final int status;
}
