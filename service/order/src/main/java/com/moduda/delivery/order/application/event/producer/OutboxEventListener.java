package com.moduda.delivery.order.application.event.producer;

import static com.moduda.delivery.order.exception.ServiceExceptionCode.NOT_FOUND_OUTBOX_EVENT;

import com.moduda.delivery.order.domain.entitiy.OutBoxEvent;
import com.moduda.delivery.order.domain.repository.OutboxEventRepository;
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

  private final OutboxEventRepository outboxEventRepository;
  private final KafkaTemplate<String ,String> kafkaTemplate;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleEvent(OutboxRecorded event) {
    log.info("[order - OutboxEventListener] 커밋 이벤트 수신");
    OutBoxEvent outBox = outboxEventRepository.findByEventId(event.getEventId())
        .orElseThrow(() -> new ServiceException(NOT_FOUND_OUTBOX_EVENT));

    String topic = event.getEventType().getKafkaTopic();

    try {
      Message<String> msg = MessageBuilder
          .withPayload(outBox.getPayload())
          .setHeader(KafkaHeaders.TOPIC, topic)
          .setHeader(KafkaHeaders.KEY, outBox.getPartitionKey())
          .setHeader("event-id", event.getEventId().toString())
          .setHeader("event-type", outBox.getEventType())
          .setHeader("occurred-at", outBox.getOccurredAt().toString())
          .build();
      log.info(msg.getPayload());
      kafkaTemplate.send(msg);
    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }
}
