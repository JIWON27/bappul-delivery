package com.bappul.delivery.payment.adapter.request;

import com.bappul.delivery.payment.domain.entity.Currency;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentPrepareRequest {
  BigDecimal totalAmount;
  Currency currency;
}
