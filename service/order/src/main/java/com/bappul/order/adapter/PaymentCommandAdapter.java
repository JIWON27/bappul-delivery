package com.bappul.order.adapter;

import com.bappul.order.adapter.request.PaymentCreateRequest;
import com.bappul.order.port.PaymentCommandPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentCommandAdapter implements PaymentCommandPort {

  private final PaymentClient paymentClient;

  @Override
  public String fakePreparePayment(PaymentCreateRequest request) {
    return paymentClient.fakePreparePayment(request);
  }
}
