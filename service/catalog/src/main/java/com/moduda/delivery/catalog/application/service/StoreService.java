package com.moduda.delivery.catalog.application.service;

import com.moduda.delivery.catalog.application.mapper.StoreMapper;
import com.moduda.delivery.catalog.application.validator.CategoryValidator;
import com.moduda.delivery.catalog.application.validator.MenuValidator;
import com.moduda.delivery.catalog.application.validator.StoreValidator;
import com.moduda.delivery.catalog.domain.entity.Category;
import com.moduda.delivery.catalog.domain.entity.Menu;
import com.moduda.delivery.catalog.domain.entity.MenuOptionGroup;
import com.moduda.delivery.catalog.domain.entity.MenuOptionValue;
import com.moduda.delivery.catalog.domain.entity.Store;
import com.moduda.delivery.catalog.domain.repository.MenuOptionGroupRepository;
import com.moduda.delivery.catalog.domain.repository.MenuOptionValueRepository;
import com.moduda.delivery.catalog.domain.repository.MenuRepository;
import com.moduda.delivery.catalog.domain.repository.StoreRepository;
import com.moduda.delivery.catalog.web.v1.request.store.StoreRequest;
import com.moduda.delivery.catalog.web.v1.response.store.StoreResponse;
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

  @Transactional
  public void enroll(StoreRequest request, Long userId){
    Category category = categoryValidator.getCategoryById(request.getCategoryId());
    Store store = storeMapper.toStore(request, category, userId);
    storeRepository.save(store);
  }

  @Transactional(readOnly = true)
  public StoreResponse getStore(Long storeId){
    Store store = storeValidator.getStore(storeId);
    return storeMapper.toResponse(store);
  }

  @Transactional
  public void toggleStoreOpenStatus(Long storeId, boolean status){
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
