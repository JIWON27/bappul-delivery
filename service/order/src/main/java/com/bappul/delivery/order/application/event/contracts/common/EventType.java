package com.bappul.delivery.order.application.event.contracts.common;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum EventType {
  COUPON_ROLLBACK("coupon-rollback"),
  COUPON_USED("coupon-used"),

  ORDER_CANCEL("order-cancel"),
  ORDER_READY("order-ready"),
  ORDER_REJECTED("order-reject"),
  ;

  final String kafkaTopic;

  EventType(String kafkaTopic) {
    this.kafkaTopic = kafkaTopic;
  }
}
