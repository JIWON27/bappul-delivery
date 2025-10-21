package com.bappul.event.kafka;

import exception.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEventListener {

  public void handle(ConsumerRecord<String, String> record, Acknowledgment ack, Runnable processor) {
    try {
      log.info("Consume topic={} partition={} offset={} key={}",
          record.topic(), record.partition(), record.offset(), record.key());
      processor.run();
      ack.acknowledge();
    } catch (Exception e) {
      log.error("Processing failed topic={} partition={} offset={} key={}",
          record.topic(), record.partition(), record.offset(), record.key(), e);
      throw new ServiceException();
    }
  }
}
