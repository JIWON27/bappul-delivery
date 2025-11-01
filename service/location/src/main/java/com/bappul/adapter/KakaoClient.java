package com.bappul.adapter;

import com.bappul.config.KakaoOpenFeignConfig;
import com.bappul.web.v1.request.KakaoAddressQuery;
import com.bappul.web.v1.response.KakaoLocalResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
    name = "kakao",
    url = "${kakao.local.base-path}",
    configuration = KakaoOpenFeignConfig.class
)
public interface KakaoClient {
  @GetMapping
  KakaoLocalResponse getLocation(@SpringQueryMap KakaoAddressQuery params);
}
