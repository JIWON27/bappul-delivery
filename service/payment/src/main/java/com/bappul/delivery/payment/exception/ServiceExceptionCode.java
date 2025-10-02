package com.bappul.delivery.payment.exception;

import exception.ServiceErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum ServiceExceptionCode implements ServiceErrorCode {
  NOT_FOUND_PAYMENT("존재하지 않는 결제정보입니다.", 404),
  IDEMPOTENCY_KEY_ERROR("멱등성 검증 오류", 409),

  NOT_FOUND_OUTBOX_EVENT("outbox 이벤트 조회 실패",404),
  INVALID_ARGUMENT_ORDER_ID("올바르지 않은 주문 ID입니다.", 400),
  JSON_SERIALIZATION_ERROR("JSON 직렬화에 실패했습니다.", 500),
  JSON_DESERIALIZATION_ERROR("JSON 역직렬화에 실패했습니다.", 400),
  ;
  final String message;
  final int status;
}
