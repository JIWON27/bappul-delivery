package com.bappul.event.kafka;

import static com.bappul.event.exception.ServiceExceptionCode.EVENT_ID_MISSING;
import static com.bappul.event.exception.ServiceExceptionCode.EVENT_PAYLOAD_MISSING;
import static com.bappul.event.exception.ServiceExceptionCode.EVENT_PROCESSING_FAILED;
import static com.bappul.event.exception.ServiceExceptionCode.EVENT_TYPE_MISSING;
import static com.bappul.event.exception.ServiceExceptionCode.JSON_DESERIALIZATION_ERROR;

import com.bappul.event.inbox.InboxStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import exception.ServiceException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaEventProcessor {

  private final InboxManager inboxManager;
  private final ObjectMapper objectMapper;

  public <T> void handleEvent(ConsumerRecord <String, String> record, Class <T> clazz, ThrowingExceptionConsumer<T> consumer){
    String eventId = parseHeader(record, "event-id");
    String eventType = parseHeader(record, "event-type");
    String payload = record.value();

    checkRequiredEventFields(eventId, eventType, payload);
    if (!inboxManager.idempotencyGuard(eventId, eventType, payload)) {
      log.info("[inbox] duplicate eventId={}, skip", eventId);
      return;
    }

    try {
      T event = parse(payload, clazz);
      consumer.accept(event);
      inboxManager.inboxWriter(eventId, InboxStatus.PROCESSED);
    } catch (JsonProcessingException e) {
      inboxManager.inboxWriter(eventId, InboxStatus.FAILED);
      throw new ServiceException(JSON_DESERIALIZATION_ERROR);
    } catch (Exception e) {
      inboxManager.inboxWriter(eventId, InboxStatus.FAILED);
      throw new ServiceException(EVENT_PROCESSING_FAILED);
    }
  }

  public <T> T parse(String payload, Class <T> clazz) throws JsonProcessingException {
    return objectMapper.readValue(payload, clazz);
  }

  public String convert(Object obj) throws JsonProcessingException {
    return objectMapper.writeValueAsString(obj);
  }

  public String parseHeader (ConsumerRecord < String, String > record, String headerKey){
    Header header = record.headers().lastHeader(headerKey);
    return header == null ? null : new String(header.value(), StandardCharsets.UTF_8);
  }

  public void checkRequiredEventFields(String eventId, String eventType, String payload) {
    if (eventId == null || eventId.isBlank()) {
      throw new ServiceException(EVENT_ID_MISSING);
    }
    if (eventType == null || eventType.isBlank()) {
      throw new ServiceException(EVENT_TYPE_MISSING);
    }
    if (payload == null || payload.isBlank()) {
      throw new ServiceException(EVENT_PAYLOAD_MISSING);
    }
  }
}
