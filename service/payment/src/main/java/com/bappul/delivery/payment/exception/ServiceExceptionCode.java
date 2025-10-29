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
  NOT_FOUND_PAYMENT_INTENT("존재하지 않는 결제 정보입니다.", 404),
  IDEMPOTENCY_KEY_ERROR("멱등성 검증 오류", 409),

  NOT_FOUND_OUTBOX_EVENT("outbox 이벤트 조회 실패",404),
  INVALID_ARGUMENT_ORDER_ID("올바르지 않은 주문 ID입니다.", 400),
  JSON_SERIALIZATION_ERROR("JSON 직렬화에 실패했습니다.", 500),
  JSON_DESERIALIZATION_ERROR("JSON 역직렬화에 실패했습니다.", 400),

  PORTONE_BAD_REQUEST("PortOne 잘못된 요청입니다.", 400),
  PORTONE_UNAUTHORIZED("PortOne 인증 실패", 401),
  PORTONE_FORBIDDEN("PortOne 접근 권한 없음", 403),
  PORTONE_PAYMENT_NOT_FOUND("PortOne 결제 건을 찾을 수 없습니다.", 404),
  PORTONE_PREPARE_FAILED("PortOne 사전등록 실패", 400),
  PORTONE_UNAVAILABLE("PortOne 서비스 일시 장애", 503),
  PORTONE_IO_ERROR("PortOne 통신 중 I/O 오류", 502),

  NOT_PAID_STATUS("결제가 완료되지 않았습니다.", 422),
  MERCHANT_UID_MISMATCH("결제 식별자가 일치하지 않습니다.", 422),
  AMOUNT_MISMATCH("결제 금액이 일치하지 않습니다.", 422),
  BUYER_EMAIL_MISMATCH("구매자 이메일이 일치하지 않습니다.", 422),
  BUYER_NAME_MISMATCH("구매자 이름이 일치하지 않습니다.", 422),
  BUYER_TEL_MISMATCH("구매자 전화번호가 일치하지 않습니다.", 422)
  ;

  final String message;
  final int status;
}
