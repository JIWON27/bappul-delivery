package com.bappul.delivery.payment.application.event.contracts.common;

import lombok.Getter;

@Getter
public enum EventType {
  PAYMENT_SUCCESS("payment-success"),
  PAYMENT_REFUNDED("payment-refunded"),
  PAYMENT_FAIL("payment-failed")
  ;

  private final String kafkaTopic;

  EventType(String kafkaTopic) {
    this.kafkaTopic = kafkaTopic;
  }
}
