package com.bappul.delivery.payment.adapter;

import com.bappul.delivery.payment.adapter.request.PaymentCancelRequest;
import com.bappul.delivery.payment.adapter.request.PaymentPrepareRequest;
import com.bappul.delivery.payment.adapter.response.PaymentPrepareResponse;
import com.bappul.delivery.payment.adapter.response.PaymentResponse;
import com.bappul.delivery.payment.domain.entity.Currency;
import com.bappul.delivery.payment.port.PortOnePort;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PortOneAdapter implements PortOnePort {

  private final PortOneClient portOneClient;

  @Override
  public PaymentResponse getPayment(String paymentId) {
    return portOneClient.getPayment(paymentId);
  }

  @Override
  public PaymentPrepareResponse preRegister(BigDecimal expectedPrice, String paymentId) {
    PaymentPrepareRequest request = PaymentPrepareRequest.builder()
        .totalAmount(expectedPrice)
        .currency(Currency.KRW)
        .build();
    return portOneClient.preRegister(request, paymentId);
  }

  @Override
  public void cancel(PaymentCancelRequest request, String paymentId) {
    portOneClient.cancle(request, paymentId);
  }
}
