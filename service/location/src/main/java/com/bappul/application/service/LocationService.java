package com.bappul.application.service;

import com.bappul.exception.ServiceExceptionCode;
import com.bappul.port.KakaoLocalPort;
import com.bappul.web.v1.request.KakaoAddressQuery;
import com.bappul.web.v1.response.AddressType;
import com.bappul.web.v1.response.KakaoLocalResponse;
import com.bappul.web.v1.response.KakaoLocalResponse.Address;
import com.bappul.web.v1.response.KakaoLocalResponse.Document;
import com.bappul.web.v1.response.KakaoLocalResponse.RoadAddress;
import com.bappul.web.v1.response.LocationResponse;
import exception.ServiceException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocationService {

  private final KakaoLocalPort kakaoLocalPort;

  public LocationResponse getLocation(KakaoAddressQuery params) {
    KakaoLocalResponse response = kakaoLocalPort.getLocation(params);
    
    if (Objects.isNull(response) || Objects.isNull(response.getDocuments()) || response.getDocuments().isEmpty()) {
      throw new ServiceException(ServiceExceptionCode.ADDRESS_NOT_FOUND);
    }

    Document Document = response.getDocuments().get(0);

    AddressType addressType = Document.getAddress_type();
    if (addressType != AddressType.ROAD_ADDR) {
      throw new ServiceException(ServiceExceptionCode.NOT_ROAD_ADDR);
    }

    Address address = Document.getAddress();
    RoadAddress roadAddress = Document.getRoad_address();

    Double longitude = Double.valueOf(address.getX());
    Double latitude = Double.valueOf(address.getX());

    if (Objects.nonNull(roadAddress.getX())) {
      longitude = Double.valueOf(roadAddress.getX());
    }

    if (Objects.nonNull(roadAddress.getY())) {
      latitude = Double.valueOf(roadAddress.getY());
    }

    return LocationResponse.builder()
        .roadAddress(roadAddress.getAddress_name())
        .jibunAddress(address.getAddress_name())
        .zoneNo(roadAddress.getZone_no())
        .haengJeongCode(address.getH_code())
        .beopJeongCode(address.getB_code())
        .latitude(longitude)
        .longitude(latitude)
        .build();
  }
}
