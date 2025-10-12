package com.bappul.catalog.application.validator;

import static com.bappul.catalog.common.exception.ServiceExceptionCode.NOT_FOUND_IMAGE;

import com.bappul.catalog.domain.entity.Store;
import com.bappul.catalog.domain.entity.StoreImage;
import com.bappul.catalog.domain.repository.StoreImageRepository;
import exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoreImageValidator {

  private final StoreImageRepository storeImageRepository;

  public StoreImage getStoreImage(Store store){
    return storeImageRepository.findByStore((store))
        .orElseThrow(() -> new ServiceException(NOT_FOUND_IMAGE));
  }

}
