package com.bappul.delivery.order.application.event.contracts.order;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderReadyEvent {
  // TODO 배달 서비스 구현 시 보완 필요
  Long orderId;
}
