package com.bappul.catalog.port;


import com.bappul.catalog.adapter.response.LocationResponse;

public interface LocationPort {
  LocationResponse getLocation(String roadAddress);
}
