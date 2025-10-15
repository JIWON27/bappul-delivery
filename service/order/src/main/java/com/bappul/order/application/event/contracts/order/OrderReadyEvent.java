package com.bappul.order.application.event.contracts.order;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderReadyEvent {
  Long orderId;
  Long storeId;
  double latitude;
  double longitude;
  String adminCode;
  String legalCode;
}
