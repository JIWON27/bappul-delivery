package com.bappul.event.kafka;

import com.bappul.event.inbox.InboxEvent;
import com.bappul.event.inbox.InboxEventRepository;
import com.bappul.event.inbox.InboxStatus;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InboxManager {

  private final Clock clock;
  private final InboxEventRepository inboxEventRepository;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void inboxWriter(String eventId, InboxStatus inboxStatus) {
    LocalDateTime processedAtUtc = LocalDateTime.now(clock.withZone(ZoneOffset.UTC));
    inboxEventRepository.updateStatusAndProcessedAt(eventId, inboxStatus, processedAtUtc);
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public boolean idempotencyGuard(String eventId, String topic, String payload){
    System.out.println("InboxManager::idempotencyGuard");
    Optional<InboxEvent> inboxEvent = inboxEventRepository.findByEventId(eventId);
    if (inboxEvent.isPresent()) {
      return false;
    }
    inboxEventRepository.save(InboxEvent.builder()
        .eventId(eventId)
        .eventType(topic)
        .payload(payload)
        .status(InboxStatus.RECEIVED)
        .build());
    return true;
  }
}
