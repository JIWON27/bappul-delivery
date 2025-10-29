package com.bappul.delivery.payment.web.v1.request;

import com.bappul.delivery.payment.domain.entity.PayMethod;
import com.bappul.delivery.payment.domain.entity.PgProvider;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class PaymentIntentRequest {
  Long orderId;
  String orderName;
  PgProvider pgProvider;
  PayMethod payMethod;
  BigDecimal expectedPrice;
}
