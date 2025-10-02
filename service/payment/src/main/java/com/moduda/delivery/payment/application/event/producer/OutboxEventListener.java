package com.moduda.delivery.payment.application.event.producer;

import static com.moduda.delivery.payment.exception.ServiceExceptionCode.NOT_FOUND_OUTBOX_EVENT;

import com.moduda.delivery.payment.domain.entity.OutBoxEvent;
import com.moduda.delivery.payment.domain.repository.OutboxRepository;
import exception.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxEventListener {

  private final OutboxRepository outboxRepository;
  private final KafkaTemplate<String, String> kafkaTemplate;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleEvent(OutboxRecorded event) {
    log.info("[payment - OutboxEventListener] 커밋 이벤트 수신 {}", event.getEventId());
    OutBoxEvent outBox = outboxRepository.findByEventId(event.getEventId())
        .orElseThrow(() -> new ServiceException(NOT_FOUND_OUTBOX_EVENT));

    String topic = event.getEventType().getKafkaTopic();

    try {
      Message<String> msg = MessageBuilder
          .withPayload(outBox.getPayload())
          .setHeader(KafkaHeaders.TOPIC, topic)
          .setHeader(KafkaHeaders.KEY, outBox.getPartitionKey())
          .setHeader("event-id", event.getEventId().toString())
          .setHeader("event-type", event.getEventType().name())
          .build();
      kafkaTemplate.send(msg);
    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }
}
