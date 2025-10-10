package com.bappul.catalog.application.validator;

import static com.bappul.catalog.common.exception.ServiceExceptionCode.ACCESS_DENIED;
import static com.bappul.catalog.common.exception.ServiceExceptionCode.NOT_FOUND_STORE;

import com.bappul.catalog.domain.entity.Store;
import com.bappul.catalog.domain.repository.StoreRepository;
import exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoreValidator {

  private final StoreRepository storeRepository;

  public Store getStore(Long storeId) {
    return storeRepository.findById(storeId)
        .orElseThrow(()->new ServiceException(NOT_FOUND_STORE));
  }

  public Store getStore(Long storeId, Long userId) {
    return storeRepository.findByIdAndUserId(storeId, userId)
        .orElseThrow(()->new ServiceException(NOT_FOUND_STORE));
  }

  public void validateStoreOwner(Store store, Long userId) {
    if (!store.getUserId().equals(userId)) {
      throw new ServiceException(ACCESS_DENIED);
    }
  }
}
