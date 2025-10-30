package com.bappul.order.port;

import com.bappul.order.adapter.request.PayMethod;
import com.bappul.order.adapter.request.PgProvider;
import java.math.BigDecimal;

public interface PaymentCommandPort {
  String createPaymentIntent(Long orderId, BigDecimal payablePrice,PgProvider pg, PayMethod pgMethod);
}
