package com.bappul.order.port;

import java.math.BigDecimal;

public interface PaymentCommandPort {
  String fakePreparePayment(Long orderId, BigDecimal payablePrice);
}
