package com.bappul.event.outbox;

import static com.bappul.event.exception.ServiceExceptionCode.EVENT_PROCESSING_FAILED;
import static com.bappul.event.exception.ServiceExceptionCode.JSON_SERIALIZATION_ERROR;

import com.bappul.event.kafka.KafkaEventProcessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import exception.ServiceException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OutboxRecorder {

  private final Clock clock;
  private final KafkaEventProcessor eventProcessor;
  private final OutboxEventRepository outboxEventRepository;
  private final ApplicationEventPublisher eventPublisher;

  public <T> void record(
      String eventType,
      String aggregateType,
      String topic,
      Long aggregateId,
      String partitionKey,
      ThrowingExceptionSupplier<T> supplier
  ) {
    try {
      LocalDateTime occurredAt = LocalDateTime.now(clock.withZone(ZoneOffset.UTC));

      UUID eventId = UUID.randomUUID();
      T event = supplier.get();
      String payload = eventProcessor.convert(event);

      outboxEventRepository.save(OutBoxEvent.builder()
          .eventId(eventId)
          .eventType(eventType)
          .topic(topic)
          .aggregateId(aggregateId)
          .aggregateType(aggregateType)
          .partitionKey(partitionKey)
          .payload(payload)
          .status(OutboxStatus.PENDING)
          .occurredAt(occurredAt)
          .build());

      eventPublisher.publishEvent(new OutboxRecorded(eventId));
    } catch (JsonProcessingException e) {
      throw new ServiceException(JSON_SERIALIZATION_ERROR);
    } catch (Exception e) {
      throw new ServiceException(EVENT_PROCESSING_FAILED);
    }
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public <T> void recordFail(
      String eventType,
      String aggregateType,
      String topic,
      Long aggregateId,
      String partitionKey,
      ThrowingExceptionSupplier<T> supplier
  ) {
    try {
      LocalDateTime occurredAt = LocalDateTime.now(clock.withZone(ZoneOffset.UTC));

      UUID eventId = UUID.randomUUID();
      T event = supplier.get();
      String payload = eventProcessor.convert(event);

      outboxEventRepository.save(OutBoxEvent.builder()
          .eventId(eventId)
          .eventType(eventType)
          .topic(topic)
          .aggregateId(aggregateId)
          .aggregateType(aggregateType)
          .partitionKey(partitionKey)
          .payload(payload)
          .status(OutboxStatus.PENDING)
          .occurredAt(occurredAt)
          .build());

      eventPublisher.publishEvent(new OutboxRecorded(eventId));
    } catch (JsonProcessingException e) {
      throw new ServiceException(JSON_SERIALIZATION_ERROR);
    } catch (Exception e) {
      throw new ServiceException(EVENT_PROCESSING_FAILED);
    }
  }
}
