package com.moduda.delivery.cart.domain.repository;

import com.moduda.delivery.cart.domain.entity.Cart;
import com.moduda.delivery.cart.domain.entity.Status;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
  Optional<Cart> findByUserIdAndStoreIdAndStatus(Long userId, Long storeId, Status status);
  Optional<Cart> findByUserId(Long userId);
  Boolean existsByUserId(Long userId);
}
