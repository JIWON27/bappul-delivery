package com.moduda.delivery.user.common.validator;

import static com.moduda.delivery.user.common.exception.ServiceExceptionCode.NOT_FOUND_ADDRESS;

import com.moduda.delivery.user.domain.entity.address.Address;
import com.moduda.delivery.user.domain.repository.AddressRepository;
import exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressValidator {
  private final AddressRepository addressRepository;

  public Address getAddress(Long userId){
    return addressRepository.findByUserId(userId)
        .orElseThrow(() -> new ServiceException(NOT_FOUND_ADDRESS));
  }

}
