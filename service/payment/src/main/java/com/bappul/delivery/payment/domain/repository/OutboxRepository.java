package com.bappul.delivery.payment.domain.repository;

import com.bappul.delivery.payment.domain.entity.OutBoxEvent;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OutboxRepository extends JpaRepository<OutBoxEvent, Long> {
  Optional<OutBoxEvent> findByEventId(UUID eventId);

}
