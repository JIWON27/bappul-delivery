package com.moduda.delivery.order.domain.repository;

import com.moduda.delivery.order.domain.entitiy.InboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InboxEventRepository extends JpaRepository<InboxEvent, Long> {
  boolean existsByEventId(String eventId);
  InboxEvent findByEventId(String eventId);
}
