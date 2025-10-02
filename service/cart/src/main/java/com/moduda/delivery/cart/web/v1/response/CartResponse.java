package com.moduda.delivery.cart.web.v1.response;

import com.moduda.delivery.cart.domain.entity.Cart;
import java.math.BigDecimal;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartResponse {
  Long cartId;
  Long storeId;
  String storeName;
  List<CartItemResponse> cartItems;
  BigDecimal totalPrice;

  public static CartResponse from(Cart cart, List<CartItemResponse> cartItems, BigDecimal totalPrice) {
    return CartResponse.builder()
        .cartId(cart.getId())
        .storeId(cart.getStoreId())
        .storeName(cart.getStoreName())
        .cartItems(cartItems)
        .totalPrice(totalPrice)
        .build();

  }
}
