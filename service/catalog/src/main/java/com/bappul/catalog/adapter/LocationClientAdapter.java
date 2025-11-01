package com.bappul.catalog.adapter;

import com.bappul.catalog.adapter.request.AddressQuery;
import com.bappul.catalog.adapter.response.LocationResponse;
import com.bappul.catalog.port.LocationPort;
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
