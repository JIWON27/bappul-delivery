package com.bappul.delivery.payment.application.event.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.bappul.delivery.payment.application.event.contracts.common.AggregateType;
import com.bappul.delivery.payment.application.event.contracts.common.EventType;
import com.bappul.delivery.payment.application.event.contracts.payment.OrderCancelEvent;
import com.bappul.delivery.payment.application.event.contracts.payment.PaymentRefundedEvent;
import com.bappul.delivery.payment.application.event.producer.OutboxRecorded;
import com.bappul.delivery.payment.application.service.RefundService;
import com.bappul.delivery.payment.application.validator.PaymentValidator;
import com.bappul.delivery.payment.domain.entity.OutBoxEvent;
import com.bappul.delivery.payment.domain.entity.OutboxStatus;
import com.bappul.delivery.payment.domain.entity.Payment;
import com.bappul.delivery.payment.domain.repository.OutboxRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KafkaEventProcessor {

  private final ObjectMapper objectMapper;
  private final OutboxRepository outboxRepository;
  private final ApplicationEventPublisher eventPublisher;
  private final PaymentValidator paymentValidator;
  private final RefundService refundService;

  @Transactional
  public void processOrderCancel(String payload){
    try {
      OrderCancelEvent event = objectMapper.readValue(payload, OrderCancelEvent.class);
      Payment payment = paymentValidator.getPaymentByOrderId(event.getOrderId());
      String finalImpUid =  payment.getImpUid();

      // fake 환불 서비스 호출 -> 환불 성공했다고 가정
      refundService.fakeCancelPayment(finalImpUid);

      payment.markAsRefunded();
      OutboxRecorded outboxRecorded = recordPaymentRefundedEvent(payment.getMerchantUid(), payment.getOrderId(), payment.getId());
      eventPublisher.publishEvent(outboxRecorded);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  private OutboxRecorded recordPaymentRefundedEvent(String merchantUid, Long orderId, Long paymentId) {
    try {
      PaymentRefundedEvent event = PaymentRefundedEvent.builder()
          .merchantUid(merchantUid)
          .orderId(orderId)
          .build();

      String payload = objectMapper.writeValueAsString(event);

      UUID uuid = UUID.randomUUID();
      outboxRepository.save(OutBoxEvent.builder()
          .eventId(uuid)
          .eventType(EventType.PAYMENT_REFUNDED)
          .aggregateId(paymentId)
          .aggregateType(AggregateType.PAYMENT)
          .partitionKey(merchantUid)
          .payload(payload)
          .status(OutboxStatus.PENDING)
          .occurredAt(LocalDateTime.now())
          .build());
      return new OutboxRecorded(uuid, EventType.PAYMENT_REFUNDED);

    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
