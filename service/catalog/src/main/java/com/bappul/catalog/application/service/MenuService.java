package com.bappul.catalog.application.service;

import com.bappul.catalog.application.mapper.MenuMapper;
import com.bappul.catalog.application.mapper.PricingMapper;
import com.bappul.catalog.application.validator.MenuValidator;
import com.bappul.catalog.application.validator.StoreValidator;
import com.bappul.catalog.domain.entity.Menu;
import com.bappul.catalog.domain.entity.MenuOptionGroup;
import com.bappul.catalog.domain.entity.MenuOptionValue;
import com.bappul.catalog.domain.entity.Store;
import com.bappul.catalog.domain.repository.MenuOptionGroupRepository;
import com.bappul.catalog.domain.repository.MenuOptionValueRepository;
import com.bappul.catalog.domain.repository.MenuRepository;
import com.bappul.catalog.web.v1.request.menu.MenuRequest;
import com.bappul.catalog.web.v1.request.menu.OptionItemRequest;
import com.bappul.catalog.web.v1.request.menu.OptionValueRequest;
import com.bappul.catalog.web.v1.request.menu.internal.CartItemRequest;
import com.bappul.catalog.web.v1.request.menu.internal.PricingInternalRequest;
import com.bappul.catalog.web.v1.response.menu.MenuOptionResponse;
import com.bappul.catalog.web.v1.response.menu.MenuOptionSetResponse;
import com.bappul.catalog.web.v1.response.menu.MenuOptionValueResponse;
import com.bappul.catalog.web.v1.response.menu.MenuResponse;
import com.bappul.catalog.web.v1.response.menu.MenuSummaryResponse;
import com.bappul.catalog.web.v1.response.menu.internal.CartItemCalculateResponse;
import com.bappul.catalog.web.v1.response.menu.internal.OptionPrice;
import com.bappul.catalog.web.v1.response.menu.internal.PricingInternalResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MenuService {

  private final MenuImageService menuImageService;

  private final MenuRepository menuRepository;
  private final MenuOptionGroupRepository menuOptionGroupRepository;
  private final MenuOptionValueRepository menuOptionValueRepository;

  private final StoreValidator storeValidator;
  private final MenuValidator menuValidator;

  private final MenuMapper menuMapper;
  private final PricingMapper pricingMapper;

  @Transactional(readOnly = true)
  public MenuResponse getMenu(Long menuId) {
    Menu menu = menuValidator.getMenu(menuId);

    List<MenuOptionSetResponse> menuOptionSetResponses = new ArrayList<>();

    List<MenuOptionValue> optionValues = menuOptionValueRepository.findAllByMenuWithGroup(menu);

    LinkedHashMap<String, List<MenuOptionValue>> menuOptionMap = optionValues.stream()
        .collect(Collectors.groupingBy(
            optionValue -> optionValue.getMenuOptionGroup().getName(),
            LinkedHashMap::new,
            Collectors.toList())
        );

    menuOptionMap.forEach((key, value) -> {
      List<MenuOptionValueResponse> menuOptionValueResponses = value.stream()
          .map(menuMapper::toMenuOptionValueResponse)
          .toList();
      MenuOptionSetResponse menuOptionSetResponse = menuMapper.toMenuOptionSetResponse(key, menuOptionValueResponses);
      menuOptionSetResponses.add(menuOptionSetResponse);
    });

    List<String> imageUrls = menuImageService.getMenuImageUrls(menu);
    String thumbnail = menuImageService.getThumbnailUrl(menu.getId());

    return menuMapper.toResponse(menu, MenuOptionResponse.from(menuOptionSetResponses), thumbnail, imageUrls);
  }

  @Transactional(readOnly = true)
  public List<MenuSummaryResponse> getMenus(Long storeId){
    Store store = storeValidator.getStore(storeId);
    List<Menu> menus = menuRepository.findAllByStore(store);

    return menus.stream()
        .map(menu -> menuMapper.toSummaryResponse(
            menu,
            menuImageService.getThumbnailUrl(menu.getId())))
        .toList();
  }

  @Transactional
  public void enroll(Long storeId, MenuRequest request, Long userId) {

    Store store = storeValidator.getStore(storeId, userId);

    Menu menu = menuMapper.toMenu(store, request);
    menuRepository.save(menu);

    for (OptionItemRequest optionItemRequest : request.getOptionRequest().getOptionItemRequests()) {
      MenuOptionGroup menuOptionGroup = menuMapper.toMenuOptionGroup(optionItemRequest, menu);
      menuOptionGroupRepository.save(menuOptionGroup);

      List<OptionValueRequest> optionValues = optionItemRequest.getOptionValues();
      List<MenuOptionValue> menuOptionValues = new ArrayList<>();

      for (OptionValueRequest optionValueRequest : optionValues) {
        MenuOptionValue menuOptionValue = menuMapper.toMenuOptionValue(optionValueRequest, menuOptionGroup);
        menuOptionValues.add(menuOptionValue);
      }
      menuOptionValueRepository.saveAll(menuOptionValues);
    }
  }

  @Transactional
  public void setMenuSoldOut(Long menuId, boolean soldOut, Long ownerId) {
    Menu menu = menuValidator.getMenu(menuId);
    Store store = menu.getStore();
    storeValidator.validateStoreOwner(store, ownerId);

    if (menu.getSoldOut() == soldOut) return;

    menu.updateSoldOut(soldOut);
  }

  @Transactional
  public void setMenuOptionSoldOut(Long menuOptionValueId, boolean soldOut, Long ownerId) {
    MenuOptionValue menuOptionValue = menuValidator.getMenuOptionValue(menuOptionValueId);
    MenuOptionGroup menuOptionGroup = menuOptionValue.getMenuOptionGroup();
    Menu menu = menuOptionGroup.getMenu();
    Store store = menu.getStore();
    storeValidator.validateStoreOwner(store, ownerId);

    if (menuOptionValue.getSoldOut() == soldOut) return;

    menuOptionValue.updateSoldOut(soldOut);
  }

  @Transactional
  public void deleteById(Long menuId) {
    Menu menu = menuValidator.getMenu(menuId);
    List<MenuOptionGroup> optionGroups = menuValidator.getMenuOptionGroup(menu);

    for (MenuOptionGroup optionGroup : optionGroups) {
      List<MenuOptionValue> optionValues = menuValidator.getMenuOptionValues(optionGroup);
      menuOptionValueRepository.deleteAll(optionValues);
    }

    menuOptionGroupRepository.deleteAll(optionGroups);
    menuRepository.delete(menu);
  }

  @Transactional
  public PricingInternalResponse calculate(PricingInternalRequest request){
    Long storeId = request.getStoreId();
    Store store = storeValidator.getStore(storeId);

    List<CartItemRequest> cartItemRequests = request.getItems();
    List<CartItemCalculateResponse> cartItemCalculateResponses = new ArrayList<>();

    BigDecimal totalPrice = BigDecimal.ZERO;

    for (CartItemRequest cartItemRequest : cartItemRequests) {
      Long menuId = cartItemRequest.getMenuId();
      Menu menu = menuValidator.getMenu(menuId);
      menuValidator.validateMenuBelongsToStore(store, menuId);

      List<Long> optionValueIds = cartItemRequest.getOptionValueIds();

      // 옵션 조회
      List<MenuOptionValue> menuOptionValues = menuOptionValueRepository.findAllById(optionValueIds);
      menuValidator.validateOptionBelongsToMenu(menuId, menuOptionValues);

      List<OptionPrice> optionPrices = menuOptionValues.stream()
          .map(value -> new OptionPrice(value.getId(), value.getName(), value.getAdditionalPrice()))
          .toList();

      // 계산
      BigDecimal basePrice = menu.getPrice();
      BigDecimal optionUnitPrice = menuOptionValues.stream()
          .map(MenuOptionValue::getAdditionalPrice)
          .reduce(BigDecimal.ZERO, BigDecimal::add);
      BigDecimal unitPrice = basePrice.add(optionUnitPrice);
      BigDecimal lineTotalPrice = unitPrice.multiply(BigDecimal.valueOf(cartItemRequest.getQuantity()));

      CartItemCalculateResponse cartItemCalculateResponse = pricingMapper.toCartItemCalculateResponse(
          menu,
          basePrice,
          optionPrices,
          optionUnitPrice,
          unitPrice,
          cartItemRequest.getQuantity(),
          lineTotalPrice
      );
      cartItemCalculateResponses.add(cartItemCalculateResponse);

      totalPrice = totalPrice.add(lineTotalPrice);
    }

    return pricingMapper.toPricingInternalResponse(store, cartItemCalculateResponses, totalPrice);
  }
}
