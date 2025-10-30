package com.bappul.order.application.event.contracts.order;

import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderCancelEvent {
  Long orderId;
  Reason reason;
  BigDecimal totalRefundPrice; // 환불 가격
}
