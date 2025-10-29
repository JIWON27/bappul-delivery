package com.bappul.delivery.payment.web.v1.response;

import com.bappul.delivery.payment.domain.entity.Currency;
import com.bappul.delivery.payment.domain.entity.PayMethod;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentIntentResponse {
  String storeId;
  String channelKey;
  String paymentId;
  String orderName;
  PayMethod payMethod;
  BigDecimal expectedPrice; // totalAmount
  Currency currency;
}
