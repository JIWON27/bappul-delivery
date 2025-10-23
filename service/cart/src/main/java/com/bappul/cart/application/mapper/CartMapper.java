package com.bappul.cart.application.mapper;

import com.bappul.cart.adapter.pricing.response.CartItemCalculateResponse;
import com.bappul.cart.adapter.pricing.response.OptionPrice;
import com.bappul.cart.domain.entity.Cart;
import com.bappul.cart.domain.entity.CartItem;
import com.bappul.cart.domain.entity.CartItemOption;
import com.bappul.cart.domain.entity.Status;
import com.bappul.cart.web.v1.request.CartRequest;
import com.bappul.cart.web.v1.response.CartItemResponse;
import com.bappul.cart.web.v1.response.CartResponse;
import com.bappul.cart.web.v1.response.MenuOptionResponse;
import java.math.BigDecimal;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {

  @Mapping(target = "storeId", source = "request.storeId")
  @Mapping(target = "storeName", source = "request.storeName")
  @Mapping(target = "totalPrice", constant = "0")
  Cart toCart(CartRequest request, Long userId, Status status);

  @Mapping(target = "basePriceSnapshot", source = "cartItemQuote.basePrice")
  @Mapping(target = "optionPriceSnapshot", source = "cartItemQuote.optionUnitPrice")
  @Mapping(target = "unitPriceSnapshot", source = "cartItemQuote.unitPrice")
  @Mapping(target = "lineTotalSnapshot", source = "cartItemQuote.lineTotalPrice")
  CartItem toCartItem(Cart cart, CartItemCalculateResponse cartItemQuote);

  @Mapping(target = "cartId", source = "cart.id")
  @Mapping(target = "items", source = "items")
  @Mapping(target = "totalPrice", source = "totalPrice")
  CartResponse toCartResponse(Cart cart, List<CartItemResponse> items, BigDecimal totalPrice);

  @Mapping(target = "cartItem", source = "cartItem")
  @Mapping(target = "menuOptionValueId", source = "optionPrice.optionValueId")
  @Mapping(target = "menuOptionValueName", source = "optionPrice.optionName")
  @Mapping(target = "optionPriceSnapShot", source = "optionPrice.optionPrice")
  CartItemOption toCartItemOption(CartItem cartItem, OptionPrice optionPrice);

  @Mapping(target = "optionItemId", source = "cartItemOption.menuOptionValueId")
  @Mapping(target = "optionName", source = "cartItemOption.menuOptionValueName")
  @Mapping(target = "additionalPrice", source = "cartItemOption.optionPriceSnapShot")
  MenuOptionResponse toMenuOptionResponse(CartItemOption cartItemOption);

  @Mapping(target = "cartItemId", source = "cartItem.id")
  @Mapping(target = "options", source = "options")
  @Mapping(target = "menuBasePrice", source = "cartItem.basePriceSnapshot")
  @Mapping(target = "lineTotalPrice", source = "cartItem.lineTotalSnapshot")
  CartItemResponse toCartItemResponse(CartItem cartItem, List<MenuOptionResponse> options);

}
