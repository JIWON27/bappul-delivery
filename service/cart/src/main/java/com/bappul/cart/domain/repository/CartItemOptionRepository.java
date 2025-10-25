package com.bappul.cart.domain.repository;

import com.bappul.cart.domain.entity.CartItem;
import com.bappul.cart.domain.entity.CartItemOption;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemOptionRepository extends JpaRepository<CartItemOption, Long> {
  List<CartItemOption> findAllByCartItem(CartItem cartItem);
  void deleteByCartItem(CartItem cartItem);
}
