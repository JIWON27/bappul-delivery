package com.bappul.delivery.payment.adapter.response;

import com.bappul.delivery.payment.domain.entity.PayMethod;
import com.bappul.delivery.payment.domain.entity.PaymentStatus;
import com.bappul.delivery.payment.domain.entity.PgProvider;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class PaymentResponse {
  PaymentStatus status;
  String id; // paymentId
  String transactionId;
  String merchantId;
  String currency;
  Channel channel;
  Amount amount;
  PgProvider pgProvider;
  PayMethod payMethod;
  String pgTxId;
  Instant paidAt;
  String receiptUrl;

  @Builder
  @Getter
  @FieldDefaults(level = AccessLevel.PRIVATE)
  @ToString
  public static class Amount {
    BigDecimal total;
    BigDecimal paid;
  }

  @Builder
  @Getter
  @FieldDefaults(level = AccessLevel.PRIVATE)
  @ToString
  public static class Channel {
    String name;
    PgProvider pgProvider;
  }
}

