package com.bappul.cart.application.service;

import com.bappul.cart.adapter.pricing.response.CartItemCalculateResponse;
import com.bappul.cart.adapter.pricing.response.OptionPrice;
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
import com.bappul.cart.web.v1.request.CartItemRequest;
import com.bappul.cart.web.v1.request.CartRequest;
import com.bappul.cart.web.v1.response.CartItemResponse;
import com.bappul.cart.web.v1.response.CartResponse;
import com.bappul.cart.web.v1.response.MenuOptionResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
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

    Cart cart = cartRepository.findByUserIdAndStoreIdAndStatus(userId, storeId, Status.ACTIVE)
        .orElseGet(() -> cartRepository.save(cartMapper.toCart(request, userId, Status.ACTIVE)));
    List<CartItem> existingItems = cartItemRepository.findAllByCart(cart);

    List<CartItemRequest> toInsert = mergeSameCartItemRequest(request, existingItems);

    BigDecimal newTotalPrice = existingItems.stream()
        .map(CartItem::getLineTotalSnapshot)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    if (toInsert.isEmpty()) {
      cart.updateTotalPrice(newTotalPrice);
      return;
    }

    PricingInternalResponse quote = pricingPort.getQuote(storeId, toInsert);
    newTotalPrice = newTotalPrice.add(quote.getTotalPrice());

    List<CartItemOption> cartItemOptions = new ArrayList<>();
    List<CartItem> cartItems = new ArrayList<>();

    for (CartItemCalculateResponse item : quote.getItems()) {
      CartItem cartItem = cartMapper.toCartItem(cart, item);
      cartItems.add(cartItem);

      for (OptionPrice optionPrice : item.getOptionPrices()) {
        CartItemOption cartItemOption = cartMapper.toCartItemOption(cartItem, optionPrice);
        cartItemOptions.add(cartItemOption);
      }
    }
    cartItemRepository.saveAll(cartItems);
    cartItemOptionRepository.saveAll(cartItemOptions);
    cart.updateTotalPrice(newTotalPrice);
  }

  @Transactional(readOnly = true)
  public CartResponse getCartItems(Long userId) {
    Cart cart = cartValidator.getMyCart(userId);
    List<CartItem> cartItems = cartItemRepository.findAllByCart(cart);

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

      totalPrice = totalPrice.add(cartItemResponse.getLineTotalPrice());
    }

    return cartMapper.toCartResponse(cart, cartItemResponses, totalPrice);
  }

  private List<CartItemRequest> mergeSameCartItemRequest(CartRequest request, List<CartItem> cartItems) {
    List<CartItemRequest> toInsert = new ArrayList<>();

    List<CartItemRequest> cartItemRequests = request.getItems();

    for (CartItemRequest cartItemRequest : cartItemRequests) {
      Long menuId = cartItemRequest.getMenuId();
      List<Long> optionValueIds = cartItemRequest.getOptionValueIds();
      int quantity = cartItemRequest.getQuantity();

      List<CartItem> sameMenuCartItem = cartItems.stream()
          .filter(cartItem -> cartItem.getMenuId().equals(menuId)).toList();

      if (sameMenuCartItem.isEmpty()) {
        toInsert.add(cartItemRequest);
        continue;
      }

      boolean merge = false;
      Set<Long> requestOptionValueIdSet = new HashSet<>(optionValueIds);
      for (CartItem cartItem : sameMenuCartItem) {
        List<CartItemOption> cartItemOptions = cartItemOptionRepository.findAllByCartItem(cartItem);
        Set<Long> savedOptionValueIdSet = cartItemOptions.stream()
            .map(CartItemOption::getMenuOptionValueId)
            .collect(Collectors.toCollection(TreeSet::new));

        if (requestOptionValueIdSet.equals(savedOptionValueIdSet)) {
          cartItem.increasementQuantity(quantity);
          cartItem.updateLineToTalPrice(cartItem.getUnitPriceSnapshot().multiply(
              BigDecimal.valueOf(cartItem.getQuantity())));
          merge = true;
          break;
        }
      }

      if (!merge) {
        toInsert.add(cartItemRequest);
      }
    }
    return toInsert;
  }
}
