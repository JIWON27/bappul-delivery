package com.bappul.delivery.payment.web.v1.request;

import com.bappul.delivery.payment.adapter.response.TransactionType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentVerifyRequest {
  TransactionType transactionType;
  String txId;
  String paymentId;
}
