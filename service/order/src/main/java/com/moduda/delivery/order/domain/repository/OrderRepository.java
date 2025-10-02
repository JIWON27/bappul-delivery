package com.moduda.delivery.order.domain.repository;

import com.moduda.delivery.order.domain.entitiy.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
  boolean existsByIdempotencyKey(String idempotencyKey);
}
