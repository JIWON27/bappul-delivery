package com.moduda.delivery.cart.client;

import com.moduda.delivery.cart.client.request.PricingInternalRequest;
import com.moduda.delivery.cart.client.response.PricingInternalResponse;
import com.moduda.delivery.cart.config.OpenFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "catalog-service",
    configuration = OpenFeignConfig.class
)
public interface CatalogClient {

  @PostMapping("/api/v1/menu/price-snapshots")
  PricingInternalResponse calculate(@RequestBody PricingInternalRequest request);

}
