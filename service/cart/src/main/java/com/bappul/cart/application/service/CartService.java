package com.bappul.cart.application.service;

import com.bappul.cart.adapter.pricing.response.CartItemCalculateResponse;
import com.bappul.cart.adapter.pricing.response.OptionPerPrice;
import com.bappul.cart.adapter.pricing.response.PricingInternalResponse;
import com.bappul.cart.application.mapper.CartMapper;
import com.bappul.cart.application.validator.CartValidator;
import com.bappul.cart.domain.entity.Cart;
import com.bappul.cart.domain.entity.CartItem;
import com.bappul.cart.domain.entity.CartItemOption;
import com.bappul.cart.domain.entity.Status;
import com.bappul.cart.domain.repository.CartItemOptionRepository;
import com.bappul.cart.domain.repository.CartItemRepository;
import com.bappul.cart.domain.repository.CartRepository;
import com.bappul.cart.port.PricingPort;
import com.bappul.cart.web.v1.request.CartRequest;
import com.bappul.cart.web.v1.response.CartItemResponse;
import com.bappul.cart.web.v1.response.CartResponse;
import com.bappul.cart.web.v1.response.MenuOptionResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartService {

  private final CartRepository cartRepository;
  private final CartItemRepository cartItemRepository;
  private final CartItemOptionRepository cartItemOptionRepository;

  private final CartValidator cartValidator;
  private final CartMapper cartMapper;
  private final PricingPort pricingPort;

  @Transactional
  public void addCartItem(CartRequest request, Long userId) {
    Long storeId = request.getStoreId();
    cartValidator.validateAddItemSameStore(userId, storeId);

    PricingInternalResponse quote = pricingPort.getQuote(storeId, request.getItems());

    Cart cart = cartRepository.findByUserIdAndStoreIdAndStatus(userId, storeId, Status.ACTIVE)
        .orElseGet(() -> cartRepository.save(cartMapper.toCart(userId, quote, Status.ACTIVE)));

    List<CartItemOption> cartItemOptions = new ArrayList<>();
    List<CartItem> cartItems = new ArrayList<>();

    for (CartItemCalculateResponse item : quote.getItems()) {
      CartItem cartItem = cartMapper.toCartItem(cart, item);
      cartItems.add(cartItem);

      for (OptionPerPrice optionPerPrice : item.getOptionPerPrices()) {
        CartItemOption cartItemOption = cartMapper.toCartItemOption(cartItem, optionPerPrice);
        cartItemOptions.add(cartItemOption);
      }
    }
    cartItemRepository.saveAll(cartItems);
    cartItemOptionRepository.saveAll(cartItemOptions);
  }


  @Transactional(readOnly = true)
  public CartResponse getCartItems(Long userId) {
    Cart cart = cartValidator.getMyCart(userId);
    List<CartItem> cartItems = cartItemRepository.findAllByCart((cart));

    List<CartItemResponse> cartItemResponses = new ArrayList<>();
    BigDecimal totalPrice = BigDecimal.ZERO;

    for (CartItem cartItem : cartItems) {
      List<CartItemOption> cartItemOptions = cartItemOptionRepository.findAllByCartItem(cartItem);
      List<MenuOptionResponse> menuOptionResponses = new ArrayList<>();

      for (CartItemOption cartItemOption : cartItemOptions) {
        MenuOptionResponse optionSummary = cartMapper.toMenuOptionResponse(cartItemOption);
        menuOptionResponses.add(optionSummary);
      }

      CartItemResponse cartItemResponse = cartMapper.toCartItemResponse(cartItem, menuOptionResponses);
      cartItemResponses.add(cartItemResponse);

      totalPrice = totalPrice.add(cartItemResponse.getLineTotal());
    }

    return CartResponse.from(cart, cartItemResponses, totalPrice);
  }
}
