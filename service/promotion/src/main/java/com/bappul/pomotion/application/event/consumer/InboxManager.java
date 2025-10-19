package com.bappul.pomotion.application.event.consumer;

import com.bappul.pomotion.application.utils.TimeUtils;
import com.bappul.pomotion.domain.entity.InboxEvent;
import com.bappul.pomotion.domain.entity.InboxStatus;
import com.bappul.pomotion.domain.repository.InboxEventRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InboxManager {

  private final TimeUtils timeUtils;
  private final InboxEventRepository inboxEventRepository;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void inboxWriter(String eventId, InboxStatus inboxStatus) {
    inboxEventRepository.updateStatusAndProcessedAt(eventId, inboxStatus, timeUtils.now());
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public boolean idempotencyGuard(String eventId, String eventType, String payload){
    Optional<InboxEvent> inboxEvent = inboxEventRepository.findByEventId(eventId);
    if (inboxEvent.isPresent()) {
      return false;
    }
    inboxEventRepository.save(InboxEvent.builder()
        .eventId(eventId)
        .eventType(eventType)
        .payload(payload)
        .status(InboxStatus.RECEIVED)
        .build());
    return true;
  }
}
