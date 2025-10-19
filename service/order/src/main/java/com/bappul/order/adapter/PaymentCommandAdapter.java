package com.bappul.order.adapter;

import com.bappul.order.adapter.request.PaymentCreateRequest;
import com.bappul.order.port.PaymentCommandPort;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentCommandAdapter implements PaymentCommandPort {

  private final PaymentClient paymentClient;

  @Override
  public String fakePreparePayment(Long orderId, BigDecimal payablePrice) {
    PaymentCreateRequest request = PaymentCreateRequest.builder()
        .orderId(orderId)
        .payablePrice(payablePrice)
        .build();
    return paymentClient.fakePreparePayment(request);
  }
}
