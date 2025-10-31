package com.bappul.port;

import com.bappul.web.v1.request.KakaoAddressQuery;
import com.bappul.web.v1.response.KakaoLocalResponse;

public interface KakaoLocalPort {
  KakaoLocalResponse getLocation(KakaoAddressQuery params);
}
