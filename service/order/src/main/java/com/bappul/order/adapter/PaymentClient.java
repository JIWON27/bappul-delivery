package com.bappul.order.adapter;

import com.bappul.order.adapter.request.PaymentIntentRequest;
import com.bappul.order.config.OpenFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "payment-service",
    path = "${payment.base-path}/${payment.api.version}",
    configuration = OpenFeignConfig.class
)
public interface PaymentClient {
  @PostMapping("/payments/intents")
  String createPaymentIntent(@RequestBody PaymentIntentRequest request);
}
