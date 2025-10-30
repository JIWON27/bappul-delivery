package com.bappul.delivery.payment.domain.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum PgProvider {
  TOSSPAY,
  TOSSPAYMENTS,
  KAKAOPAY
  ;

  final String channelKey;

  PgProvider() {
    channelKey = this.name();
  }
}
