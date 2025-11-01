package com.bappul.adapter;

import com.bappul.port.KakaoLocalPort;
import com.bappul.web.v1.request.KakaoAddressQuery;
import com.bappul.web.v1.response.KakaoLocalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KakaoLocalAdapter implements KakaoLocalPort {

  private final KakaoClient kakaoClient;

  @Override
  public KakaoLocalResponse getLocation(KakaoAddressQuery params) {
    return kakaoClient.getLocation(params);
  }
}
