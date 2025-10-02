package com.moduda.delivery.order.application.event.contracts.common;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum EventType {
  COUPON_ROLLBACK("coupon-rollback"),
  COUPON_USED("coupon-used"),
  ;

  final String kafkaTopic;

  EventType(String kafkaTopic) {
    this.kafkaTopic = kafkaTopic;
  }
}
