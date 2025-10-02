package com.bappul.delivery.user.common.exception;

import exception.ServiceErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum ServiceExceptionCode implements ServiceErrorCode {
  LOGIN_BAD_REQUEST("아이디 또는 비밀번호를 다시 입력해주세요.", 400),

  NOT_FOUND_USER("해당 유저를 찾을 수 없습니다.", 404),
  DUPLICATE_EMAIL("이미 사용 중인 이메일입니다.", 400),
  DUPLICATE_NICKNAME("이미 사용 중인 닉네임입니다.", 400),
  INVALID_PASSWORD("비밀번호가 일치하지 않습니다.", 400),

  NOT_FOUND_ADDRESS("해당 주소를 찾을 수 없습니다.", 404)
  ;

  final String message;
  final int status;
}
