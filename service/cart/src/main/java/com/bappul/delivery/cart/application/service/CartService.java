package com.bappul.delivery.cart.application.service;

import com.bappul.delivery.cart.client.CatalogClient;
import com.bappul.delivery.cart.client.request.PricingInternalRequest;
import com.bappul.delivery.cart.client.response.CartItemCalculateResponse;
import com.bappul.delivery.cart.client.response.OptionPerPrice;
import com.bappul.delivery.cart.client.response.PricingInternalResponse;
import com.bappul.delivery.cart.common.validator.CartValidator;
import com.bappul.delivery.cart.domain.entity.Cart;
import com.bappul.delivery.cart.domain.entity.CartItem;
import com.bappul.delivery.cart.domain.entity.CartItemOption;
import com.bappul.delivery.cart.domain.entity.Status;
import com.bappul.delivery.cart.domain.repository.CartItemOptionRepository;
import com.bappul.delivery.cart.domain.repository.CartItemRepository;
import com.bappul.delivery.cart.domain.repository.CartRepository;
import com.bappul.delivery.cart.web.v1.request.CartRequest;
import com.bappul.delivery.cart.web.v1.response.CartItemResponse;
import com.bappul.delivery.cart.web.v1.response.CartResponse;
import com.bappul.delivery.cart.web.v1.response.MenuOptionSummary;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartService {

  private final CartRepository cartRepository;
  private final CartItemRepository cartItemRepository;
  private final CartItemOptionRepository cartItemOptionRepository;
  private final CartValidator cartValidator;
  private final CatalogClient catalogClient;

  @Transactional
  @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 3000))
  public void addCartItem(CartRequest request, Long userId) {
    cartValidator.validateAddItemSameStore(userId, request.getStoreId());

    PricingInternalRequest internalRequest = PricingInternalRequest.builder()
        .storeId(request.getStoreId())
        .items(request.getItems())
        .build();

    PricingInternalResponse quote = catalogClient.calculate(internalRequest);

    Long storeId = request.getStoreId();

    Cart cart = cartRepository.findByUserIdAndStoreIdAndStatus(userId, storeId, Status.ACTIVE)
        .orElseGet(() -> cartRepository.save(
            Cart.builder()
                .userId(userId)
                .storeName(quote.getStoreName())
                .storeId(storeId)
                .status(Status.ACTIVE)
                .totalPrice(quote.getTotalPrice())
                .build()
        ));

    List<CartItemOption> cartItemOptions = new ArrayList<>();
    List<CartItem> cartItems = new ArrayList<>();

    for (CartItemCalculateResponse item : quote.getItems()) {
      CartItem cartItem = CartItem.builder()
          .cart(cart)
          .menuId(item.getMenuId())
          .menuName(item.getMenuName())
          .quantity(item.getQuantity())
          .basePriceSnapshot(item.getBasePrice())
          .optionPriceSnapshot(item.getOptionUnitPrice())
          .unitPriceSnapshot(item.getUnitPrice())
          .lineTotalSnapshot(item.getLineTotal())
          .build();

      cartItems.add(cartItem);

      for (OptionPerPrice optionPerPrice : item.getOptionPerPrices()) {
        CartItemOption cartItemOption = CartItemOption.builder()
            .cartItem(cartItem)
            .menuId(item.getMenuId())
            .menuOptionValueId(optionPerPrice.getOptionValueId())
            .menuOptionValueName(optionPerPrice.getOptionName())
            .optionPriceSnapShot(optionPerPrice.getOptionPrice())
            .build();
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
      List<MenuOptionSummary> optionSummaries = new ArrayList<>();

      for (CartItemOption cartItemOption : cartItemOptions) {
        MenuOptionSummary optionSummary = MenuOptionSummary.builder()
            .optionItemId(cartItemOption.getMenuOptionValueId())
            .optionName(cartItemOption.getMenuOptionValueName())
            .optionAdditionalPrice(cartItemOption.getOptionPriceSnapShot())
            .build();
        optionSummaries.add(optionSummary);
      }

      BigDecimal basePrice = cartItem.getBasePriceSnapshot();
      BigDecimal lineTotal = cartItem.getLineTotalSnapshot();

      CartItemResponse cartItemResponse = CartItemResponse.builder()
          .cartItemId(cartItem.getId())
          .menuId(cartItem.getMenuId())
          .menuName(cartItem.getMenuName())
          .options(optionSummaries)
          .basePrice(basePrice)
          .lineTotal(lineTotal)
          .quantity(cartItem.getQuantity())
          .build();
      cartItemResponses.add(cartItemResponse);

      totalPrice = totalPrice.add(cartItemResponse.getLineTotal());
    }

    return CartResponse.from(cart, cartItemResponses, totalPrice);
  }
}
