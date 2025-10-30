package com.bappul.order.adapter.request;

import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentIntentRequest {
  Long orderId;
  String orderName;
  PgProvider pgProvider;
  PayMethod payMethod;
  BigDecimal expectedPrice;
}
