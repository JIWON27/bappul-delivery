package com.moduda.delivery.catalog.application.validator;

import static com.moduda.delivery.catalog.common.exception.ServiceExceptionCode.MENU_NOT_IN_STORE;
import static com.moduda.delivery.catalog.common.exception.ServiceExceptionCode.NOT_FOUND_MENU;
import static com.moduda.delivery.catalog.common.exception.ServiceExceptionCode.NOT_FOUND_MENU_OPTION_VALUE;
import static com.moduda.delivery.catalog.common.exception.ServiceExceptionCode.OPTION_NOT_IN_MENU;

import com.moduda.delivery.catalog.domain.entity.Menu;
import com.moduda.delivery.catalog.domain.entity.MenuOptionGroup;
import com.moduda.delivery.catalog.domain.entity.MenuOptionValue;
import com.moduda.delivery.catalog.domain.entity.Store;
import com.moduda.delivery.catalog.domain.repository.MenuOptionGroupRepository;
import com.moduda.delivery.catalog.domain.repository.MenuOptionValueRepository;
import com.moduda.delivery.catalog.domain.repository.MenuRepository;
import exception.ServiceException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MenuValidator {

  private final MenuRepository menuRepository;
  private final MenuOptionGroupRepository menuOptionGroupRepository;
  private final MenuOptionValueRepository menuOptionValueRepository;


  public Menu getMenu(Long menuId){
    return menuRepository.findById(menuId)
        .orElseThrow(() -> new ServiceException(NOT_FOUND_MENU));
  }

  public List<Menu> getMenus(Store store) {
    return menuRepository.findAllByStore(store);
  }

  public List<MenuOptionGroup> getMenuOptionGroup(Menu menu){
    return menuOptionGroupRepository.findByMenu(menu);
  }

  public MenuOptionValue getMenuOptionValue(Long menuOptionValueId) {
    return menuOptionValueRepository.findById(menuOptionValueId)
        .orElseThrow(() -> new ServiceException(NOT_FOUND_MENU_OPTION_VALUE));
  }

  public List<MenuOptionValue> getMenuOptionValues(MenuOptionGroup menuOptionGroup) {
    return menuOptionValueRepository.findAllByMenuOptionGroup(menuOptionGroup);
  }

  public void validateMenuBelongsToStore(Store store, Long menuId){
    Boolean exists = menuRepository.existsByIdAndStore(menuId, store);
    if (!exists) {
      throw new ServiceException(MENU_NOT_IN_STORE);
    }
  }

  public void validateOptionBelongsToMenu(Long menuId, List<MenuOptionValue> menuOptionValues) {
    for (MenuOptionValue value : menuOptionValues) {
      MenuOptionGroup menuOptionGroup = value.getMenuOptionGroup();
      if (!menuOptionGroup.getMenu().getId().equals(menuId)){
        throw new ServiceException(OPTION_NOT_IN_MENU);
      }
    }
  }
}
