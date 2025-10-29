package com.bappul.order.adapter;

import com.bappul.order.adapter.request.PayMethod;
import com.bappul.order.adapter.request.PaymentIntentRequest;
import com.bappul.order.adapter.request.PgProvider;
import com.bappul.order.port.PaymentCommandPort;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentCommandAdapter implements PaymentCommandPort {

  private final PaymentClient paymentClient;

  @Override
  public String createPaymentIntent(
      Long orderId,
      BigDecimal payablePrice,
      PgProvider pgProvider,
      PayMethod payMethod)
  {
    // TODO storeName, menuName 등 테이블에 컬럼 추가하면 그때 생성하는 메서드 추가
    String orderName = "TEST";

    PaymentIntentRequest request = PaymentIntentRequest.builder()
        .orderId(orderId)
        .orderName(orderName)
        .expectedPrice(payablePrice)
        .pgProvider(pgProvider)
        .payMethod(payMethod)
        .build();
    return paymentClient.createPaymentIntent(request);
  }
}
