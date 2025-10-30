package com.bappul.delivery.payment.adapter;

import com.bappul.delivery.payment.adapter.request.PaymentCancelRequest;
import com.bappul.delivery.payment.adapter.request.PaymentPrepareRequest;
import com.bappul.delivery.payment.adapter.response.PaymentPrepareResponse;
import com.bappul.delivery.payment.adapter.response.PaymentResponse;
import com.bappul.delivery.payment.config.PortOneOpenFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "portone",
    url = "${portone.v2.base-path}",
    configuration = PortOneOpenFeignConfig.class
)
public interface PortOneClient {

  @GetMapping("/payments/{paymentId}")
  PaymentResponse getPayment(@PathVariable String paymentId);

  @PostMapping("/payments/{paymentId}/pre-register")
  PaymentPrepareResponse preRegister(@RequestBody PaymentPrepareRequest request, @PathVariable String paymentId);

  @PostMapping("/payments/{paymentId}/cancel")
  void cancle(@RequestBody PaymentCancelRequest request, @PathVariable String paymentId);
}
