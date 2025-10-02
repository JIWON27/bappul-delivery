package com.moduda.delivery.user.web.dto;

import com.moduda.delivery.user.domain.entity.address.Address;
import com.moduda.delivery.user.domain.entity.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressRequest {
  String alias;
  String zipcode;
  String roadAddress;
  String detailAddress;
  Boolean isDefault;

  public Address toEntity(User user){
    return Address.builder()
        .user(user)
        .alias(alias)
        .zipcode(zipcode)
        .roadAddress(roadAddress)
        .detailAddress(detailAddress)
        .isDefault(isDefault)
        .build();
  }
}
