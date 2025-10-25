package com.bappul.cart.domain.repository;

import com.bappul.cart.domain.entity.Cart;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
  Optional<Cart> findByUserId(Long userId);
  Boolean existsByUserId(Long userId);
}
