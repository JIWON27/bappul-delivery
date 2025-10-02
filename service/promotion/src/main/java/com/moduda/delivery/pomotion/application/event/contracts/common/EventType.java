package com.moduda.delivery.pomotion.application.event.contracts.common;

import lombok.Getter;

@Getter
public enum EventType {
  FIRST_COME_COUPON_ISSUE("first-come-coupon-request")
  ;

  private final String kafkaTopic;

  EventType(String kafkaTopic) {
    this.kafkaTopic = kafkaTopic;
  }
}
