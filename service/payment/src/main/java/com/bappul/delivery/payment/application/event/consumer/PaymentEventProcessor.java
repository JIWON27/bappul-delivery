package com.bappul.delivery.payment.application.event.consumer;

import com.bappul.delivery.payment.adapter.request.PaymentCancelRequest;
import com.bappul.delivery.payment.application.event.contracts.common.AggregateType;
import com.bappul.delivery.payment.application.event.contracts.common.EventType;
import com.bappul.delivery.payment.application.event.contracts.payment.OrderCancelEvent;
import com.bappul.delivery.payment.application.event.contracts.payment.PaymentRefundedEvent;
import com.bappul.delivery.payment.application.validator.PaymentValidator;
import com.bappul.delivery.payment.domain.entity.Payment;
import com.bappul.delivery.payment.port.PortOnePort;
import com.bappul.event.kafka.KafkaEventProcessor;
import com.bappul.event.outbox.OutboxRecorder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentEventProcessor {

  private final KafkaEventProcessor eventProcessor;
  private final OutboxRecorder outboxRecorder;
  private final PaymentValidator paymentValidator;
  private final PortOnePort portOnePort;

  @Transactional
  public void processOrderCancel(ConsumerRecord<String, String> record) {
    eventProcessor.handleEvent(record, OrderCancelEvent.class, this::processOrderCancelEventLogic);
  }

  private void processOrderCancelEventLogic(OrderCancelEvent event) {
    System.out.println(event.toString());
    Payment payment = paymentValidator.getPaymentByOrderId(event.getOrderId());
    String paymentId = payment.getPaymentId();

    PaymentCancelRequest request = PaymentCancelRequest.builder()
        .reason(event.getReason().getContent())
        .currentCancellableAmount(payment.getApprovedPrice().intValue())
        .amount(event.getTotalRefundPrice().intValue())
        .build();

    portOnePort.cancel(request, paymentId);
    payment.markAsRefunded();

    outboxRecorder.record(
        EventType.PAYMENT_REFUNDED.name(),
        AggregateType.PAYMENT.name(),
        EventType.PAYMENT_REFUNDED.getKafkaTopic(),
        payment.getOrderId(),
        payment.getOrderId().toString(),
        () -> PaymentRefundedEvent.builder()
            .paymentId(paymentId)
            .orderId(payment.getOrderId())
            .build()
    );
  }
}
