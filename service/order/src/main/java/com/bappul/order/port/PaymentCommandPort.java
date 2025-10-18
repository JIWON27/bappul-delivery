package com.bappul.order.port;

import com.bappul.order.adapter.request.PaymentCreateRequest;

public interface PaymentCommandPort {
  String fakePreparePayment(PaymentCreateRequest request);
}
