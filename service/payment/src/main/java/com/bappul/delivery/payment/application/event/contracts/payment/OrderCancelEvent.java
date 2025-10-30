package com.bappul.delivery.payment.application.event.contracts.payment;

import com.bappul.delivery.payment.adapter.request.Reason;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderCancelEvent {
  Long orderId;
  Reason reason;
  BigDecimal totalRefundPrice; // 환불 가격
}
