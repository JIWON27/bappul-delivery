package com.bappul.delivery.domain.repository;

import com.bappul.delivery.domain.entity.OutBoxEvent;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutBoxEvent, Long> {
  Optional<OutBoxEvent> findByEventId(UUID eventId);
}
