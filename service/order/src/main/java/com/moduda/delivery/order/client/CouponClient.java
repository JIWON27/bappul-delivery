package com.moduda.delivery.order.client;

import com.moduda.delivery.order.client.request.CouponDiscountCalculateRequest;
import com.moduda.delivery.order.client.response.CouponDiscountCalculateResponse;
import com.moduda.delivery.order.config.OpenFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "promotion-service",
    path = "${coupon.base-path}/${coupon.api.version}",
    configuration = OpenFeignConfig.class
)
public interface CouponClient {

  @PostMapping("/coupons/discount")
  CouponDiscountCalculateResponse getCouponDiscountCalculate(@RequestBody CouponDiscountCalculateRequest request);

}
