package com.bappul.delivery.domain.repository;

import com.bappul.delivery.domain.entity.Delivery;
import com.bappul.delivery.domain.entity.DeliveryStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

  Optional<Delivery> findByOrderId(Long orderId);

  @Modifying
  @Query("UPDATE Delivery d SET d.riderUserId = :riderId, d.status = :status WHERE d.id = :deliveryId and d.riderUserId IS NULL")
  int tryAssign(
      @Param("deliveryId") Long deliveryId,
      @Param("riderId") Long riderId,
      @Param("status") DeliveryStatus status
  );
}
