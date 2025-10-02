package com.bappul.delivery.order.web.v1.response;

import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {
  Long orderId;
  String merchantUid;
  BigDecimal orderTotal;
}
