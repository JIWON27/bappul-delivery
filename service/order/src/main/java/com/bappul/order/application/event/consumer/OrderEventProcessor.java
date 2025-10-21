package com.bappul.order.application.event.consumer;

import com.bappul.event.kafka.KafkaEventProcessor;
import com.bappul.event.outbox.OutboxRecorder;
import com.bappul.order.application.event.contracts.common.AggregateType;
import com.bappul.order.application.event.contracts.common.EventType;
import com.bappul.order.application.event.contracts.coupon.CouponEvent;
import com.bappul.order.application.event.contracts.delivery.DeliveryCompleteEvent;
import com.bappul.order.application.event.contracts.delivery.DeliveryPickUpEvent;
import com.bappul.order.application.event.contracts.payment.PaymentFailEvent;
import com.bappul.order.application.event.contracts.payment.PaymentRefundedEvent;
import com.bappul.order.application.event.contracts.payment.PaymentSuccessEvent;
import com.bappul.order.application.validator.OrderValidator;
import com.bappul.order.domain.entitiy.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderEventProcessor {

  private final KafkaEventProcessor eventProcessor;
  private final OrderValidator orderValidator;
  private final OutboxRecorder outboxRecorder;

  @Transactional
  public void processPaymentSuccess(ConsumerRecord<String, String> record) {
    eventProcessor.handleEvent(record, PaymentSuccessEvent.class, this::processPaymentSuccessLogic);
  }

  @Transactional
  public void processPaymentFail(ConsumerRecord<String, String> record) {
    eventProcessor.handleEvent(record, PaymentFailEvent.class, this::processPaymentFailedLogic);
  }

  @Transactional
  public void processPaymentRefund(ConsumerRecord<String, String> record) {
    eventProcessor.handleEvent(record, PaymentRefundedEvent.class, this::processPaymentRefundLogic);
  }

  @Transactional
  public void processDeliveryPickUp(ConsumerRecord<String, String> record) {
    eventProcessor.handleEvent(record, DeliveryPickUpEvent.class, this::processDeliveryPickUpLogic);
  }

  @Transactional
  public void processDeliveryComplete(ConsumerRecord<String, String> record) {
    eventProcessor.handleEvent(record, DeliveryCompleteEvent.class, this::processDeliveryCompleteLogic);
  }

  private void processPaymentSuccessLogic(PaymentSuccessEvent event) {
    Order order = orderValidator.getOrderById(event.getOrderId());

    outboxRecorder.record(
        EventType.COUPON_USED.name(),
        AggregateType.ORDER.name(),
        EventType.COUPON_USED.getKafkaTopic(),
        order.getId(),
        order.getId().toString(),
        () -> CouponEvent.builder()
            .eventType(EventType.COUPON_USED.name())
            .orderId(order.getId())
            .couponId(order.getCouponId())
            .userId(order.getUserId())
            .build()
    );
    order.markAsPaid();
  }

  private void processPaymentFailedLogic(PaymentFailEvent event) {
    Order order = orderValidator.getOrderById(event.getOrderId());
    order.markAsCanceled();
  }

  private void processPaymentRefundLogic(PaymentRefundedEvent event) {
    Order order = orderValidator.getOrderById(event.getOrderId());
    outboxRecorder.record(
        EventType.COUPON_ROLLBACK.name(),
        AggregateType.ORDER.name(),
        EventType.COUPON_ROLLBACK.getKafkaTopic(),
        order.getId(),
        order.getId().toString(),
        () -> CouponEvent.builder()
            .eventType(EventType.COUPON_ROLLBACK.name())
            .orderId(order.getId())
            .couponId(order.getCouponId())
            .userId(order.getUserId())
            .build()
    );
    order.markAsRefunded();
  }

  private void processDeliveryPickUpLogic(DeliveryPickUpEvent event) {
    Order order = orderValidator.getOrderById(event.getOrderId());
    order.markAsPickUp();
  }

  private void processDeliveryCompleteLogic(DeliveryCompleteEvent event) {
    Order order = orderValidator.getOrderById(event.getOrderId());
    order.markAsCompleted();
  }
}
