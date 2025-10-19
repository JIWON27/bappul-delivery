package com.bappul.order.domain.repository;

import com.bappul.order.domain.entitiy.InboxEvent;
import com.bappul.order.domain.entitiy.InboxStatus;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InboxEventRepository extends JpaRepository<InboxEvent, Long> {
  Optional<InboxEvent> findByEventId(String eventId);

  @Modifying
  @Query("update InboxEvent e set e.status = :status, e.processedAt = :processedAt where e.eventId = :eventId")
  void updateStatusAndProcessedAt(
      @Param("eventId") String eventId,
      @Param("status") InboxStatus status,
      @Param("processedAt") LocalDateTime processedAt);
}
