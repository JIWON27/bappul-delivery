package com.bappul.delivery.domain.repository;

import com.bappul.delivery.domain.entity.InboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InboxEventRepository extends JpaRepository<InboxEvent, Long> {
  boolean existsByEventId(String eventId);
  InboxEvent findByEventId(String eventId);
}
