package com.moduda.delivery.cart.domain.repository;

import com.moduda.delivery.cart.domain.entity.CartItem;
import com.moduda.delivery.cart.domain.entity.CartItemOption;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemOptionRepository extends JpaRepository<CartItemOption, Long> {
  List<CartItemOption> findAllByCartItem(CartItem cartItem);
}
