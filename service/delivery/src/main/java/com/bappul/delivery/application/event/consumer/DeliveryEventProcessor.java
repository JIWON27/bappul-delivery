package com.bappul.delivery.application.event.consumer;

import com.bappul.delivery.application.event.contracts.order.OrderAcceptEvent;
import com.bappul.delivery.application.event.contracts.order.OrderReadyEvent;
import com.bappul.delivery.application.service.RiderRedisService;
import com.bappul.delivery.application.validator.DeliveryValidator;
import com.bappul.delivery.domain.entity.Delivery;
import com.bappul.delivery.domain.entity.DeliveryStatus;
import com.bappul.delivery.domain.repository.DeliveryRepository;
import com.bappul.delivery.exception.ServiceExceptionCode;
import com.bappul.event.kafka.KafkaEventProcessor;
import com.netflix.servo.util.VisibleForTesting;
import exception.ServiceException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeliveryEventProcessor {

  private final DeliveryRepository deliveryRepository;
  private final KafkaEventProcessor eventProcessor;
  private final DeliveryValidator deliveryValidator;
  private final RiderRedisService riderRedisService;

  @Transactional
  public void processOrderAcceptEvent(ConsumerRecord<String, String> record) {
    eventProcessor.handleEvent(record, OrderAcceptEvent.class, this::processOrderAcceptEventLogic);
  }

  private void processOrderAcceptEventLogic(OrderAcceptEvent event) {
    Delivery delivery = Delivery.builder()
        .orderId(event.getOrderId())
        .riderUserId(null)
        .status(DeliveryStatus.REQUESTED)
        .build();
    deliveryRepository.save(delivery);
  }

  @Transactional
  public void processOrderReadyEvent(ConsumerRecord<String, String> record) {
    eventProcessor.handleEvent(record, OrderReadyEvent.class, this::processOrderReadyEventLogic);
  }

  @VisibleForTesting
  void processOrderReadyEventLogic(OrderReadyEvent event) {
    final double INITIAL_RADIUS = 0.5;
    final int RIDER_LIMIT = 3;
    final int MAX_TRIES = 3;
    final int MAX_RIDER_LIMIT = 15;

    Delivery delivery = deliveryValidator.getByOrderId(event.getOrderId());
    if (delivery.getStatus() == DeliveryStatus.DISPATCHING ||
        delivery.getStatus() == DeliveryStatus.ASSIGNED ||
        delivery.getStatus() == DeliveryStatus.PICKED_UP ||
        delivery.getStatus() == DeliveryStatus.DELIVERED) {
      return;
    }

    double storeLatitude = event.getLatitude();
    double storeLongitude = event.getLongitude();
    List<Long> riderIds = new ArrayList<>();

    Point storePoint = new Point(storeLongitude, storeLatitude);

    double radius = INITIAL_RADIUS;
    int riderLimit =  RIDER_LIMIT;
    for (int i=0; i<MAX_TRIES; i++) {
      riderIds = riderRedisService.findNearbyRiders(storePoint, radius, riderLimit);
      if (!riderIds.isEmpty()) break;

      radius *=  2.0;
      riderLimit = Math.min(MAX_RIDER_LIMIT, riderLimit * 2);
    }

    if (riderIds.isEmpty()) {
      throw new ServiceException(ServiceExceptionCode.NO_NEARBY_RIDER);
    }

    delivery.markAsDispatching();

    // TODO 추출한 nearestRiderIds 라이더들에게 배달 알림
  }
}
