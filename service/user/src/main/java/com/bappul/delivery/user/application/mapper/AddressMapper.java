package com.bappul.delivery.user.application.mapper;

import com.bappul.delivery.user.domain.entity.address.Address;
import com.bappul.delivery.user.web.dto.AddressResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressMapper {

  AddressResponse toAddressResponse(Address address);
}
