package com.moduda.delivery.order.client;

import com.moduda.delivery.order.client.request.PricingInternalRequest;
import com.moduda.delivery.order.client.response.PricingInternalResponse;
import com.moduda.delivery.order.config.OpenFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "catalog-service",
    path = "${catalog.base-path}/${coupon.api.version}",
    configuration = OpenFeignConfig.class
)
public interface CatalogClient {

  @PostMapping("/menus/price-snapshots")
  PricingInternalResponse calculate(@RequestBody PricingInternalRequest request);

}
