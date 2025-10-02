package com.moduda.delivery.order.client;

import com.moduda.delivery.order.client.request.PaymentCreateRequest;
import com.moduda.delivery.order.config.OpenFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "payment-service",
    path = "${payment.base-path}/${payment.api.version}",
    configuration = OpenFeignConfig.class
)
public interface PaymentClient {

  @PostMapping("/payments/fake/prepare")
  String fakePreparePayment(@RequestBody PaymentCreateRequest request);

}
