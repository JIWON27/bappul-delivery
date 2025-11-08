package com.bappul.order.adapter;

import com.bappul.order.adapter.request.PricingInternalRequest;
import com.bappul.order.adapter.response.PricingInternalResponse;
import com.bappul.order.adapter.response.StoreLocationResponse;
import com.bappul.order.config.OpenFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

  @GetMapping("/stores/{storeId}/location")
  StoreLocationResponse getLocation(@PathVariable Long storeId);

}
