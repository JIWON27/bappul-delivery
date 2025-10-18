package com.bappul.order.adapter;

import com.bappul.order.adapter.request.CouponDiscountCalculateRequest;
import com.bappul.order.adapter.response.CouponDiscountCalculateResponse;
import com.bappul.order.config.OpenFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "promotion-service",
    path = "${coupon.base-path}/${coupon.api.version}",
    configuration = OpenFeignConfig.class
)
public interface PromotionClient {

  @PostMapping("/coupons/discount")
  CouponDiscountCalculateResponse getCouponDiscountCalculate(@RequestBody CouponDiscountCalculateRequest request);

}
