package com.bappul.cart.application.mapper;

import com.bappul.cart.client.response.CartItemCalculateResponse;
import com.bappul.cart.client.response.OptionPerPrice;
import com.bappul.cart.client.response.PricingInternalResponse;
import com.bappul.cart.domain.entity.Cart;
import com.bappul.cart.domain.entity.CartItem;
import com.bappul.cart.domain.entity.CartItemOption;
import com.bappul.cart.domain.entity.Status;
import com.bappul.cart.web.v1.response.CartItemResponse;
import com.bappul.cart.web.v1.response.MenuOptionResponse;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {

  @Mapping(target = "storeId", source = "quote.storeId")
  @Mapping(target = "storeName", source = "quote.storeName")
  @Mapping(target = "totalPrice", source = "quote.totalPrice")
  Cart toCart(Long userId, PricingInternalResponse quote, Status status);

  @Mapping(target = "basePriceSnapshot", source = "cartItemQuote.basePrice")
  @Mapping(target = "optionPriceSnapshot", source = "cartItemQuote.optionUnitPrice")
  @Mapping(target = "unitPriceSnapshot", source = "cartItemQuote.unitPrice")
  @Mapping(target = "lineTotalSnapshot", source = "cartItemQuote.lineTotal")
  CartItem toCartItem(Cart cart, CartItemCalculateResponse cartItemQuote);

  @Mapping(target = "cartItem", source = "cartItem")
  @Mapping(target = "menuOptionValueId", source = "optionPerPrice.optionValueId")
  @Mapping(target = "menuOptionValueName", source = "optionPerPrice.optionName")
  @Mapping(target = "optionPriceSnapShot", source = "optionPerPrice.optionPrice")
  CartItemOption toCartItemOption(CartItem cartItem, OptionPerPrice optionPerPrice);

  @Mapping(target = "optionItemId", source = "cartItemOption.menuOptionValueId")
  @Mapping(target = "optionName", source = "cartItemOption.menuOptionValueName")
  @Mapping(target = "optionAdditionalPrice", source = "cartItemOption.optionPriceSnapShot")
  MenuOptionResponse toMenuOptionResponse(CartItemOption cartItemOption);

  @Mapping(target = "options", source = "options")
  @Mapping(target = "basePrice", source = "cartItem.basePriceSnapshot")
  @Mapping(target = "lineTotal", source = "cartItem.lineTotalSnapshot")
  CartItemResponse toCartItemResponse(CartItem cartItem, List<MenuOptionResponse> options);

}
