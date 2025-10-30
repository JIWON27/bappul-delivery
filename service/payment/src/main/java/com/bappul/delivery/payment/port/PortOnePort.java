package com.bappul.delivery.payment.port;

import com.bappul.delivery.payment.adapter.request.PaymentCancelRequest;
import com.bappul.delivery.payment.adapter.response.PaymentPrepareResponse;
import com.bappul.delivery.payment.adapter.response.PaymentResponse;
import java.math.BigDecimal;

public interface PortOnePort {
  PaymentResponse getPayment(String paymentId);
  PaymentPrepareResponse preRegister(BigDecimal expectedPrice, String paymentId);
  void cancel(PaymentCancelRequest request, String paymentId);
}
