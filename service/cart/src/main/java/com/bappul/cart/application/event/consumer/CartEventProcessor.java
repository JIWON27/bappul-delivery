package com.bappul.cart.application.event.consumer;

import com.bappul.cart.application.event.contracts.cart.CartClearEvent;
import com.bappul.cart.application.validator.CartValidator;
import com.bappul.cart.domain.entity.Cart;
import com.bappul.cart.domain.entity.CartItem;
import com.bappul.cart.domain.repository.CartItemOptionRepository;
import com.bappul.cart.domain.repository.CartItemRepository;
import com.bappul.cart.domain.repository.CartRepository;
import com.bappul.event.kafka.KafkaEventProcessor;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartEventProcessor {

  private final KafkaEventProcessor kafkaEventProcessor;
  private final CartValidator cartValidator;
  private final CartRepository cartRepository;
  private final CartItemRepository cartItemRepository;
  private final CartItemOptionRepository cartItemOptionRepository;

  @Transactional
  public void processCartClearEvent(ConsumerRecord<String, String> record) {
    kafkaEventProcessor.handleEvent(record, CartClearEvent.class, this::clearCart);
  }

  private void clearCart(CartClearEvent event) {
    Cart cart = cartValidator.getMyCart(event.getUserId());
    List<CartItem> cartItems = cartItemRepository.findAllByCart(cart);

    for (CartItem cartItem : cartItems) {
      cartItemOptionRepository.deleteByCartItem(cartItem);
    }
    cartItemRepository.deleteByCart(cart);
    cartRepository.delete(cart);
  }
}
