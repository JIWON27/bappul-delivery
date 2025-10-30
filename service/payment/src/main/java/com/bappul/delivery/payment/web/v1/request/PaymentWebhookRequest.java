package com.bappul.delivery.payment.web.v1.request;

import java.time.Instant;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentWebhookRequest {
  String type;
  Instant timestamp;
  Data data;

  @Getter
  @Builder
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class Data {
    String storeId;
    String paymentId;
    String transactionId;
  }
}
