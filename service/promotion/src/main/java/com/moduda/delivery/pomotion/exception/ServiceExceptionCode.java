package com.moduda.delivery.pomotion.exception;

import exception.ServiceErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum ServiceExceptionCode implements ServiceErrorCode {
  // coupon 관련
  NOT_FOUND_COUPON("쿠폰이 존재하지 않습니다.", 404),
  INVALID_COUPON_CODE("유효하지 않은 쿠폰 코드입니다.", 400),
  ALREADY_USED_COUPON("이미 사용한 쿠폰입니다.", 409),
  ALREADY_ISSUED_COUPON("이미 발급받은 쿠폰입니다.", 409),
  NOT_FOUND_COUPON_POLICY("쿠폰 정책이 존재하지 않습니다.", 404),
  EXCEEDED_COUPON_ISSUE_LIMIT("해당 쿠폰은 더 이상 발급할 수 없습니다", 409),

  COUPON_NOT_OWNED("쿠폰 소유자가 아닙니다.", 403),
  COUPON_EXPIRED("해당 쿠폰이 만료되었습니다.", 500),
  COUPON_ALREADY_USED("이미 사용한 쿠폰입니다.", 409),

  ORDER_MIN_TOTAL_NOT_MET("최소 주문 금액을 채워주세요", 400),

  POLICY_INACTIVE("해당 쿠폰 정책은 활성화되어있지 않습니다. ", 400),

  JSON_SERIALIZATION_ERROR("JSON 직렬화에 실패했습니다.", 500),
  JSON_DESERIALIZATION_ERROR("JSON 역직렬화에 실패했습니다.", 400),

  ;
  final String message;
  final int status;
}
