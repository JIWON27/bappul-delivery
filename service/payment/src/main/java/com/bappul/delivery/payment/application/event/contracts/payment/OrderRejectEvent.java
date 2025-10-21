package com.bappul.delivery.payment.application.event.contracts.payment;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderRejectEvent {
  Long orderId;
  Long userId;
}
