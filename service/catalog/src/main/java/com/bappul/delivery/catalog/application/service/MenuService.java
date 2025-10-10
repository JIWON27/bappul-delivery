package com.bappul.delivery.catalog.application.service;

import com.bappul.delivery.catalog.application.mapper.MenuMapper;
import com.bappul.delivery.catalog.application.validator.MenuValidator;
import com.bappul.delivery.catalog.application.validator.StoreValidator;
import com.bappul.delivery.catalog.domain.entity.Menu;
import com.bappul.delivery.catalog.domain.entity.MenuOptionGroup;
import com.bappul.delivery.catalog.domain.entity.MenuOptionValue;
import com.bappul.delivery.catalog.domain.entity.Store;
import com.bappul.delivery.catalog.domain.repository.MenuOptionGroupRepository;
import com.bappul.delivery.catalog.domain.repository.MenuOptionValueRepository;
import com.bappul.delivery.catalog.domain.repository.MenuRepository;
import com.bappul.delivery.catalog.web.v1.request.menu.MenuRequest;
import com.bappul.delivery.catalog.web.v1.request.menu.OptionItemRequest;
import com.bappul.delivery.catalog.web.v1.request.menu.OptionRequest;
import com.bappul.delivery.catalog.web.v1.request.menu.OptionValueRequest;
import com.bappul.delivery.catalog.web.v1.request.menu.internal.CartItemRequest;
import com.bappul.delivery.catalog.web.v1.request.menu.internal.PricingInternalRequest;
import com.bappul.delivery.catalog.web.v1.response.menu.MenuOptionResponse;
import com.bappul.delivery.catalog.web.v1.response.menu.MenuOptionSetResponse;
import com.bappul.delivery.catalog.web.v1.response.menu.MenuOptionValueResponse;
import com.bappul.delivery.catalog.web.v1.response.menu.MenuResponse;
import com.bappul.delivery.catalog.web.v1.response.menu.MenuSummaryResponse;
import com.bappul.delivery.catalog.web.v1.response.menu.internal.CartItemCalculateResponse;
import com.bappul.delivery.catalog.web.v1.response.menu.internal.OptionPerPrice;
import com.bappul.delivery.catalog.web.v1.response.menu.internal.PricingInternalResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

    return menuMapper.toResponse(menu, MenuOptionResponse.from(menuOptionSetResponses));
  }

  @Transactional(readOnly = true)
  public List<MenuSummaryResponse> getMenus(Long storeId){
    Store store = storeValidator.getStore(storeId);
    List<Menu> menus = menuRepository.findAllByStore(store);

    // TODO 메뉴 이미지 기능 구현 시 이미지 경로 따로 넘기도록 수정 필요
    return menus.stream()
        .map(menuMapper::toSummaryResponse).toList();
  }

  @Transactional
  public void enroll(Long storeId, MenuRequest request, OptionRequest optionRequest, MultipartFile image, Long userId) {

    Store store = storeValidator.getStore(storeId, userId);

    // TODO 메뉴 이미지 기능 구현 시 수정해야할 부분
    String photoUrl = menuImageService.saveImage(image);

    Menu menu = menuMapper.toMenu(store, request, photoUrl);
    Menu savedMenu = menuRepository.save(menu);

    for (OptionItemRequest optionItemRequest : optionRequest.getOptionItemRequests()) {
      MenuOptionGroup menuOptionGroup = MenuOptionGroup.builder()
          .menu(savedMenu)
          .name(optionItemRequest.getGroupName())
          .sortOrder(1) // TODO setOrder 로직 추가
          .build();
      MenuOptionGroup savedMenuOptionGroup = menuOptionGroupRepository.save(menuOptionGroup);

      List<OptionValueRequest> optionValues = optionItemRequest.getOptionValues();
      List<MenuOptionValue> menuOptionValues = new ArrayList<>();

      for  (OptionValueRequest optionValue : optionValues) {
        MenuOptionValue menuOptionValue = MenuOptionValue.builder()
            .menuOptionGroup(savedMenuOptionGroup)
            .name(optionValue.getName())
            .additionalPrice(optionValue.getAdditionalPrice())
            .build();
        menuOptionValues.add(menuOptionValue);
      }
      menuOptionValueRepository.saveAll(menuOptionValues);
    }
  }

  @Transactional
  public void toggleMenuSoldOut(Long menuId, boolean soldOut, Long ownerId) {
    Menu menu = menuValidator.getMenu(menuId);
    Store store = menu.getStore();
    storeValidator.validateStoreOwner(store, ownerId);

    if (menu.getSoldOut() == soldOut) return;

    menu.updateSoldOut(soldOut);
  }

  @Transactional
  public void toggleOptionStatus(Long menuOptionValueId, boolean soldOut, Long ownerId) {
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

      List<OptionPerPrice> optionPerPrices = menuOptionValues.stream()
          .map(value -> new OptionPerPrice(value.getId(), value.getName(), value.getAdditionalPrice())).toList();

      // 계산
      BigDecimal basePrice = menu.getPrice();
      BigDecimal optionUnitPrice = menuOptionValues.stream()
          .map(MenuOptionValue::getAdditionalPrice)
          .reduce(BigDecimal.ZERO, BigDecimal::add);
      BigDecimal unitPrice = basePrice.add(optionUnitPrice);
      BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(cartItemRequest.getQuantity()));

      CartItemCalculateResponse cartItemCalculateResponse = CartItemCalculateResponse.builder()
          .menuId(menuId)
          .menuName(menu.getName())
          .basePrice(basePrice)
          .optionPerPrices(optionPerPrices)
          .optionUnitPrice(optionUnitPrice)
          .unitPrice(unitPrice)
          .quantity(cartItemRequest.getQuantity())
          .lineTotal(lineTotal)
          .build();
      cartItemCalculateResponses.add(cartItemCalculateResponse);

      totalPrice = totalPrice.add(lineTotal);
    }

    return PricingInternalResponse.builder()
        .storeId(storeId)
        .storeName(store.getName())
        .items(cartItemCalculateResponses)
        .totalPrice(totalPrice)
        .build();
  }
}
