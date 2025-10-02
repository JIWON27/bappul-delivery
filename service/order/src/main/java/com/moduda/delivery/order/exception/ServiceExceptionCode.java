package com.moduda.delivery.order.exception;

import exception.ServiceErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum ServiceExceptionCode implements ServiceErrorCode {
  NOT_FOUND_ORDER("주문 정보를 찾을 수 없습니다.", 404),
  NOT_FOUND_ORDER_MENU("해당 메뉴에 대한 주문 정보를 찾을 수 없습니다.", 404),
  UNAUTHORIZED_ORDER_ACCESS("해당 주문은 접근 권한이 없습니다.", 403),
  CANNOT_CANCEL_ORDER_STATUS("주문이 이미 처리되어 취소할 수 없습니다.", 400),

  NOT_FOUND_OUTBOX_EVENT("존재하지 않는 아웃박스 이벤트 입니다.", 500),
  JSON_SERIALIZATION_ERROR("JSON 직렬화에 실패했습니다.", 500),
  JSON_DESERIALIZATION_ERROR("JSON 역직렬화에 실패했습니다.", 400),
  IDEMPOTENCYKEY("멱등성 검증 오류", 500),

  ;
  final String message;
  final int status;
}
