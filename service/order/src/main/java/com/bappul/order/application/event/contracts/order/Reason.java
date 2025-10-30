package com.bappul.order.application.event.contracts.order;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum Reason {
  USER_REQUEST("회원의 취소 요청"),
  OWNER_REQUEST("점주의 취소 요청")
  ;

  final String content;
  Reason(String content) {
    this.content = content;
  }
}
