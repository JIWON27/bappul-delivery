package com.bappul.delivery.user.adapter;

import com.bappul.delivery.user.adapter.request.AddressQuery;
import com.bappul.delivery.user.adapter.response.LocationResponse;
import com.bappul.delivery.user.config.OpenFeignConfig;
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
