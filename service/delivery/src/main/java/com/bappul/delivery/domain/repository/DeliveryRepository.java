package com.bappul.delivery.domain.repository;

import com.bappul.delivery.domain.entity.Delivery;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

  Optional<Delivery> findByOrderId(Long orderId);
}
