package com.moduda.delivery.user.application.mapper;

import com.moduda.delivery.user.domain.entity.address.Address;
import com.moduda.delivery.user.web.dto.AddressResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressMapper {

  AddressResponse toAddressResponse(Address address);
}
