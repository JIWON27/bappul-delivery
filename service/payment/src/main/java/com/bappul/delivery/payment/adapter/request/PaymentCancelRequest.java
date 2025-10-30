package com.bappul.delivery.payment.adapter.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class PaymentCancelRequest {
  String reason;
  Integer currentCancellableAmount;
  Integer amount;
}
