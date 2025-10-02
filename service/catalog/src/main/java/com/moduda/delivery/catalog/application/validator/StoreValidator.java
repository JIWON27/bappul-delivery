package com.moduda.delivery.catalog.application.validator;

import static com.moduda.delivery.catalog.common.exception.ServiceExceptionCode.ACCESS_DENIED;
import static com.moduda.delivery.catalog.common.exception.ServiceExceptionCode.NOT_FOUND_STORE;

import com.moduda.delivery.catalog.domain.entity.Store;
import com.moduda.delivery.catalog.domain.repository.StoreRepository;
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
