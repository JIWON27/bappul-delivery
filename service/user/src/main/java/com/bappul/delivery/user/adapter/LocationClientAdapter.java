package com.bappul.delivery.user.adapter;

import com.bappul.delivery.user.adapter.request.AddressQuery;
import com.bappul.delivery.user.adapter.response.LocationResponse;
import com.bappul.delivery.user.port.LocationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LocationClientAdapter implements LocationPort {

  private final LocationClient locationClient;

  @Override
  public LocationResponse getLocation(String roadAddress) {
    AddressQuery query = AddressQuery.builder()
        .query(roadAddress)
        .build();
    return locationClient.getLocation(query);
  }
}
