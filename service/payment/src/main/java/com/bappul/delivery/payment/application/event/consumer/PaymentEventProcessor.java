package com.bappul.delivery.payment.application.event.consumer;

import com.bappul.delivery.payment.application.event.contracts.common.AggregateType;
import com.bappul.delivery.payment.application.event.contracts.common.EventType;
import com.bappul.delivery.payment.application.event.contracts.payment.OrderCancelEvent;
import com.bappul.delivery.payment.application.event.contracts.payment.OrderRejectEvent;
import com.bappul.delivery.payment.application.event.contracts.payment.PaymentRefundedEvent;
import com.bappul.delivery.payment.application.service.RefundService;
import com.bappul.delivery.payment.application.validator.PaymentValidator;
import com.bappul.delivery.payment.domain.entity.Payment;
import com.bappul.event.kafka.KafkaEventProcessor;
import com.bappul.event.outbox.OutboxRecorder;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentEventProcessor {

  private final KafkaEventProcessor eventProcessor;
  private final OutboxRecorder outboxRecorder;
  private final PaymentValidator paymentValidator;
  private final RefundService refundService;

  @Transactional
  public void processOrderCancel(ConsumerRecord<String, String> record) {
    eventProcessor.handleEvent(record, OrderCancelEvent.class, this::processOrderAcceptEventLogic);
  }

  @Transactional
  public void processOrderReject(ConsumerRecord<String, String> record) {
    eventProcessor.handleEvent(record, OrderRejectEvent.class, this::processOrderAcceptEventLogic);
  }

  private void processOrderAcceptEventLogic(OrderCancelEvent event) {
    Payment payment = paymentValidator.getPaymentByOrderId(event.getOrderId());
    String impUid = payment.getImpUid();

    // Fake 환불 서비스 호출
    refundService.fakeCancelPayment(impUid);

    payment.markAsRefunded();

    outboxRecorder.record(
        EventType.PAYMENT_REFUNDED.name(),
        AggregateType.PAYMENT.name(),
        EventType.PAYMENT_REFUNDED.getKafkaTopic(),
        payment.getOrderId(),
        payment.getOrderId().toString(),
        () -> PaymentRefundedEvent.builder()
            .merchantUid(payment.getMerchantUid())
            .orderId(payment.getOrderId())
            .build()
    );
  }

  private void processOrderAcceptEventLogic(OrderRejectEvent event) {
    Payment payment = paymentValidator.getPaymentByOrderId(event.getOrderId());
    String impUid = payment.getImpUid();

    // Fake 환불 서비스 호출
    refundService.fakeCancelPayment(impUid);

    payment.markAsRefunded();

    outboxRecorder.record(
        EventType.PAYMENT_REFUNDED.name(),
        AggregateType.PAYMENT.name(),
        EventType.PAYMENT_REFUNDED.getKafkaTopic(),
        payment.getOrderId(),
        payment.getOrderId().toString(),
        () -> PaymentRefundedEvent.builder()
            .merchantUid(payment.getMerchantUid())
            .orderId(payment.getOrderId())
            .build()
    );
  }
}
