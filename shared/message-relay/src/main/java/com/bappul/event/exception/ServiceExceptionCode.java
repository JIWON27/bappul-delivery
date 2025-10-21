package com.bappul.event.exception;

import exception.ServiceErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum ServiceExceptionCode implements ServiceErrorCode {
  NOT_FOUND_OUTBOX_EVENT("존재하지 않는 아웃박스 이벤트 입니다.", 500),
  JSON_SERIALIZATION_ERROR("JSON 직렬화에 실패했습니다.", 500),
  JSON_DESERIALIZATION_ERROR("JSON 역직렬화에 실패했습니다.", 400),
  EVENT_ID_MISSING("이벤트 ID(event-id)가 누락되었습니다.", 400),
  EVENT_TYPE_MISSING("이벤트 타입(event-type)이 누락되었습니다.", 400),
  EVENT_PAYLOAD_MISSING("이벤트 페이로드가 누락되었습니다.", 400),
  EVENT_PROCESSING_FAILED("이벤트 처리 중 오류가 발생했습니다.", 500);
  ;

  final String message;
  final int status;
}
