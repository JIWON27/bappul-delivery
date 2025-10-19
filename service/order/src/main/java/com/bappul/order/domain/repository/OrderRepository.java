package com.bappul.order.domain.repository;

import com.bappul.order.domain.entitiy.Order;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
  boolean existsByIdempotencyKey(String idempotencyKey);
  Optional<Order> findByIdempotencyKey(String idempotencyKey);
}
