package com.bappul.catalog.application.service;

import com.bappul.catalog.adapter.response.LocationResponse;
import com.bappul.catalog.application.mapper.StoreMapper;
import com.bappul.catalog.application.validator.CategoryValidator;
import com.bappul.catalog.application.validator.MenuValidator;
import com.bappul.catalog.application.validator.StoreValidator;
import com.bappul.catalog.domain.entity.Category;
import com.bappul.catalog.domain.entity.Menu;
import com.bappul.catalog.domain.entity.MenuOptionGroup;
import com.bappul.catalog.domain.entity.MenuOptionValue;
import com.bappul.catalog.domain.entity.Store;
import com.bappul.catalog.domain.repository.MenuOptionGroupRepository;
import com.bappul.catalog.domain.repository.MenuOptionValueRepository;
import com.bappul.catalog.domain.repository.MenuRepository;
import com.bappul.catalog.domain.repository.StoreRepository;
import com.bappul.catalog.port.LocationPort;
import com.bappul.catalog.web.v1.request.store.StoreRequest;
import com.bappul.catalog.web.v1.response.store.StoreLocationResponse;
import com.bappul.catalog.web.v1.response.store.StoreResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StoreService {

  private final StoreRepository storeRepository;
  private final MenuRepository menuRepository;
  private final MenuOptionGroupRepository menuOptionGroupRepository;
  private final MenuOptionValueRepository menuOptionValueRepository;

  private final StoreValidator storeValidator;
  private final MenuValidator menuValidator;
  private final CategoryValidator categoryValidator;

  private final StoreMapper storeMapper;
  private final LocationPort locationPort;

  @Transactional
  public void enroll(StoreRequest request){
    Category category = categoryValidator.getCategoryById(request.getCategoryId());
    LocationResponse location = locationPort.getLocation(request.getRoadAddress());
    Store store = storeMapper.toStore(request, category, location);
    storeRepository.save(store);
  }

  @Transactional(readOnly = true)
  public StoreResponse getStore(Long storeId){
    Store store = storeValidator.getStore(storeId);
    return storeMapper.toResponse(store);
  }

  @Transactional(readOnly = true)
  public StoreLocationResponse getStoreLocation(Long storeId){
    Store store = storeValidator.getStore(storeId);
    return storeMapper.toStoreLocationResponse(store);
  }

  @Transactional
  public void setStoreOpenStatus(Long storeId, boolean status){
    Store store = storeValidator.getStore(storeId);
    store.updateOpenStatus(status);
  }

  @Transactional
  public void deleteById(Long storeId){
    Store store = storeValidator.getStore(storeId);
    List<Menu> menus = menuValidator.getMenus(store);
    for (Menu menu : menus) {
      List<MenuOptionGroup> optionGroups = menuValidator.getMenuOptionGroup(menu);
      for (MenuOptionGroup optionGroup : optionGroups) {
        List<MenuOptionValue> optionValues = menuValidator.getMenuOptionValues(optionGroup);
        menuOptionValueRepository.deleteAll(optionValues);
      }
      menuOptionGroupRepository.deleteAll(optionGroups);
    }
    menuRepository.deleteAll(menus);
    storeRepository.delete(store);
  }
}
