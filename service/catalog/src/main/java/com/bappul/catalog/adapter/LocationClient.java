package com.bappul.catalog.adapter;

import com.bappul.catalog.adapter.request.AddressQuery;
import com.bappul.catalog.adapter.response.LocationResponse;
import com.bappul.catalog.config.OpenFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
    name = "location-service",
    path = "${location.base-path}",
    configuration = OpenFeignConfig.class
)
public interface LocationClient {

  @GetMapping
  LocationResponse getLocation(@SpringQueryMap AddressQuery query);

}
