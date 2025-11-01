package com.bappul.delivery.user.application.mapper;

import com.bappul.delivery.user.adapter.response.LocationResponse;
import com.bappul.delivery.user.domain.entity.address.Address;
import com.bappul.delivery.user.domain.entity.user.User;
import com.bappul.delivery.user.web.dto.AddressRequest;
import com.bappul.delivery.user.web.dto.AddressResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AddressMapper {

  @Mapping(target = "jibunAddress", source = "location.jibunAddress")
  @Mapping(target = "haengjeongCode", source = "location.haengJeongCode")
  @Mapping(target = "beopjeongCode", source = "location.beopJeongCode")
  @Mapping(target = "zipcode", source = "location.zoneNo")
  @Mapping(target = "roadAddress", source = "location.roadAddress")
  Address toAddress(User user, AddressRequest addressRequest, LocationResponse location);
  AddressResponse toAddressResponse(Address address);
}
