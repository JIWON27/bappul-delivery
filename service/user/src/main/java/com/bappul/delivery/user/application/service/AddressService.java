package com.bappul.delivery.user.application.service;

import com.bappul.delivery.user.application.mapper.AddressMapper;
import com.bappul.delivery.user.common.validator.AddressValidator;
import com.bappul.delivery.user.common.validator.UserValidator;
import com.bappul.delivery.user.domain.entity.address.Address;
import com.bappul.delivery.user.domain.entity.user.User;
import com.bappul.delivery.user.domain.repository.AddressRepository;
import com.bappul.delivery.user.web.dto.AddressRequest;
import com.bappul.delivery.user.web.dto.AddressResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AddressService {

  private final AddressMapper addressMapper;
  private final UserValidator userValidator;
  private final AddressValidator addressValidator;
  private final AddressRepository addressRepository;

  @Transactional
  public AddressResponse registerAddress(AddressRequest request, Long userId) {
    User user = userValidator.getById(userId);

    Address address = request.toEntity(user);
    Address savedAddress = addressRepository.save(address);
    return addressMapper.toAddressResponse(savedAddress);
  }

  @Transactional(readOnly = true)
  public AddressResponse getAddress(Long userId){
    Address address = addressValidator.getAddress(userId);
    return addressMapper.toAddressResponse(address);
  }
}
