package com.bappul.delivery.user.port;

import com.bappul.delivery.user.adapter.response.LocationResponse;

public interface LocationPort {
  LocationResponse getLocation(String roadAddress);
}
