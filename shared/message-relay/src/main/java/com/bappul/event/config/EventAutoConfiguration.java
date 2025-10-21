package com.bappul.event.config;

import com.bappul.event.inbox.InboxEventRepository;
import com.bappul.event.kafka.InboxManager;
import com.bappul.event.kafka.KafkaEventListener;
import com.bappul.event.kafka.KafkaEventProcessor;
import com.bappul.event.outbox.OutboxEventListener;
import com.bappul.event.outbox.OutboxEventRepository;
import com.bappul.event.outbox.OutboxRecorder;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManagerFactory;
import java.time.Clock;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

@AutoConfiguration
public class EventAutoConfiguration {

  @Bean
  @ConditionalOnBean({Clock.class, EntityManagerFactory.class})
  @ConditionalOnMissingBean
  InboxManager inboxManager(Clock clock, InboxEventRepository inboxEventRepository) {
    return new InboxManager(clock, inboxEventRepository);
  }

  @Bean
  KafkaEventListener kafkaEventListener() {
    return new KafkaEventListener();
  }

  @Bean
  @ConditionalOnBean({OutboxEventRepository.class, KafkaTemplate.class})
  @ConditionalOnMissingBean
  OutboxEventListener outboxEventListener(OutboxEventRepository outboxEventRepository, KafkaTemplate<String, String> kafkaTemplate) {
    return new OutboxEventListener(outboxEventRepository, kafkaTemplate);
  }

  @Bean
  @ConditionalOnBean({Clock.class, KafkaEventProcessor.class, OutboxEventRepository.class, ApplicationEventPublisher.class})
  @ConditionalOnMissingBean
  OutboxRecorder OutboxRecorder(Clock clock, KafkaEventProcessor eventProcessor, OutboxEventRepository outboxEventRepository, ApplicationEventPublisher eventPublisher) {
    return new OutboxRecorder(clock, eventProcessor, outboxEventRepository, eventPublisher);
  }

  @Bean
  @ConditionalOnBean({InboxManager.class, ObjectMapper.class})
  @ConditionalOnMissingBean
  KafkaEventProcessor kafkaEventProcessor(InboxManager inboxManager, ObjectMapper objectMapper) {
    return new KafkaEventProcessor(inboxManager, objectMapper);
  }
}
