package com.moduda.delivery.cart.common.validator;

import static com.moduda.delivery.cart.common.exception.ServiceExceptionCode.CART_STORE_CONFLICT;
import static com.moduda.delivery.cart.common.exception.ServiceExceptionCode.NOT_FOUND_CART;

import com.moduda.delivery.cart.domain.entity.Cart;
import com.moduda.delivery.cart.domain.repository.CartRepository;
import exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartValidator {

  private final CartRepository cartRepository;

  public Cart getCart(Long cartId){
    return cartRepository.findById(cartId)
        .orElseThrow(() -> new ServiceException(NOT_FOUND_CART));
  }

  public Cart getMyCart(Long userId){
    return cartRepository.findByUserId(userId)
        .orElseThrow(() -> new ServiceException(NOT_FOUND_CART));
  }

  public void validateAddItemSameStore(Long userId, Long storeId) {
    if (!cartRepository.existsByUserId(userId)) {
      return;
    }

    Cart cart = getMyCart(userId);
    if(!cart.getStoreId().equals(storeId)) {
      throw new ServiceException(CART_STORE_CONFLICT);
    }
  }
}
