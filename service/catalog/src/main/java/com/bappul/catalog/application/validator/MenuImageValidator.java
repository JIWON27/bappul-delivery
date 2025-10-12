package com.bappul.catalog.application.validator;

import static com.bappul.catalog.common.exception.ServiceExceptionCode.NOT_FOUND_IMAGE;

import com.bappul.catalog.domain.entity.Menu;
import com.bappul.catalog.domain.entity.MenuImage;
import com.bappul.catalog.domain.repository.MenuImageRepository;
import exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MenuImageValidator {

  private final MenuImageRepository menuImageRepository;

  public MenuImage getThumbnailImage(Menu menu){
    return menuImageRepository.findByMenuAndThumbnailTrue(menu)
        .orElseThrow(() -> new ServiceException(NOT_FOUND_IMAGE));
  }

  public MenuImage getMenuImage(Long menuImageId){
    return menuImageRepository.findById(menuImageId)
        .orElseThrow(() -> new ServiceException(NOT_FOUND_IMAGE));
  }
}
