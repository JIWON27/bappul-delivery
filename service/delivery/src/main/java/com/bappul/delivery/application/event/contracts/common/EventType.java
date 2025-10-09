package com.bappul.delivery.application.event.contracts.common;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum EventType {
  DELIVERY_PICKUP("delivery-pickup"),
  DELIVERY_COMPLETE("delivery-complete"),
  ;

  final String kafkaTopic;

  EventType(String kafkaTopic) {
    this.kafkaTopic = kafkaTopic;
  }
}
