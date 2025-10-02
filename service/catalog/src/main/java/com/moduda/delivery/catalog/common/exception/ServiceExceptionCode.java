package com.moduda.delivery.catalog.common.exception;

import exception.ServiceErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum ServiceExceptionCode implements ServiceErrorCode {

  NOT_FOUND_MENU("존재하지 않는 메뉴입니다.", 404),
  NOT_FOUND_STORE("가게가 존재하지 않습니다.", 404),
  NOT_FOUND_MENU_OPTION_GROUP("존재하지 않는 메뉴 옵션 그룹입니다.", 404),
  NOT_FOUND_MENU_OPTION_VALUE("존재하지 않는 메뉴 옵션 값입니다.", 404),
  NOT_FOUND_CATEGORY("존재하지 않는 카테고리입니다.", 404),

  MENU_NOT_IN_STORE("요청한 메뉴는 해당 가게의 메뉴가 아닙니다.", 400),
  OPTION_NOT_IN_MENU("요청한 옵션은 해당 가게의 메뉴 옵션이 아닙니다.", 400),

  CATEGORY_HAS_STORES("해당 카테고리의 가게가 존재합니다.", 400),
  ACCESS_DENIED("권한이 없습니다.", 403),

  CURSOR_INVALID_FORMAT("커서 포맷이 올바르지 않습니다.", 400),
  CURSOR_PARAM_REQUIRED("커서 생성에 필요한 값이 누락되었습니다.", 400),
  CURSOR_INTERNAL_ERROR("커서 처리 중 오류가 발생했습니다.", 500);
  ;

  final String message;
  final int status;
}
