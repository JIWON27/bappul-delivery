package com.bappul.delivery.order.client.request;

import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentCreateRequest {
  Long orderId;
  BigDecimal payablePrice;
}
