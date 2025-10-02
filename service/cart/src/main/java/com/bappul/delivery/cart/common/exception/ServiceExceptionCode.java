package com.bappul.delivery.cart.common.exception;

import exception.ServiceErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum ServiceExceptionCode implements ServiceErrorCode {
  NOT_FOUND_CART("존재하지 않는 장바구니입니다.", 404),
  NOT_FOUND_CART_ITEM("해당 장바구니 상품이 존재하지 않습니다.", 404),
  NOT_FOUND_CART_ITEM_OPTION("해당 장바구니 상품의 옵션이 존재하지 않습니다.", 404),
  QUANTITY_CANNOT_BE_NEGATIVE("수량은 음수일 수 없습니다.", 400),
  CART_STORE_CONFLICT("장바구니에는 한 가게의 메뉴만 담을 수 있습니다.",409)
  ;

  final String message;
  final int status;
}
