package com.bappul.delivery.cart.domain.repository;

import com.bappul.delivery.cart.domain.entity.Cart;
import com.bappul.delivery.cart.domain.entity.CartItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
  List<CartItem>  findAllByCart(Cart cart);
}
