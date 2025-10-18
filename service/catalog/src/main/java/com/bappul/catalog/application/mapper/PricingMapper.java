package com.bappul.catalog.application.mapper;

import com.bappul.catalog.domain.entity.Menu;
import com.bappul.catalog.domain.entity.Store;
import com.bappul.catalog.web.v1.response.menu.internal.CartItemCalculateResponse;
import com.bappul.catalog.web.v1.response.menu.internal.OptionPrice;
import com.bappul.catalog.web.v1.response.menu.internal.PricingInternalResponse;
import java.math.BigDecimal;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PricingMapper {

  @Mapping(target = "menuId", source = "menu.id")
  @Mapping(target = "menuName", source = "menu.name")
  CartItemCalculateResponse toCartItemCalculateResponse(Menu menu,
      BigDecimal basePrice,
      List<OptionPrice> optionPrices,
      BigDecimal optionUnitPrice,
      BigDecimal unitPrice,
      int quantity,
      BigDecimal lineTotal
  );

  @Mapping(target = "storeId", source = "store.id")
  @Mapping(target = "storeName", source = "store.name")
  @Mapping(target = "deliveryFeePrice", source = "store.deliveryFee")
  PricingInternalResponse toPricingInternalResponse(
      Store store,
      List<CartItemCalculateResponse> items,
      BigDecimal totalPrice
  );
}
