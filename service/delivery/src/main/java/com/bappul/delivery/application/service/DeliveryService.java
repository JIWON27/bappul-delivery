package com.bappul.delivery.application.service;

import com.bappul.delivery.application.event.contracts.common.AggregateType;
import com.bappul.delivery.application.event.contracts.common.EventType;
import com.bappul.delivery.application.event.contracts.delivery.DeliveryPickUpEvent;
import com.bappul.delivery.application.validator.DeliveryValidator;
import com.bappul.delivery.domain.entity.Delivery;
import com.bappul.delivery.web.v1.request.RiderLocation;
import com.bappul.event.outbox.OutboxRecorder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeliveryService {

  private final DeliveryValidator deliveryValidator;
  private final OutboxRecorder outboxRecorder;
  private final RiderRedisService riderRedisService;

  @Transactional
  public void deliveryAccept(Long deliveryId, Long riderUserId){
    /**
     * 배차 알고리즘 실행 후 라이더들에게 배달 배차 알람이 간 후,
     * 라이더들 중 배달 배차를 수락하면 실행될 메서드
     * TODO 배달 접수 시 동시성 고민
     */

    Delivery delivery = deliveryValidator.getByDeliveryId(deliveryId);
    delivery.updateRiderUserId(riderUserId);

    delivery.markAsAssigned();
  }

  @Transactional
  public void deliveryPickUp(Long deliveryId, Long riderUserId){
    Delivery delivery = deliveryValidator.getByDeliveryId(deliveryId);

    outboxRecorder.record(
        EventType.DELIVERY_PICKUP.name(),
        AggregateType.DELIVERY.name(),
        EventType.DELIVERY_PICKUP.getKafkaTopic(),
        delivery.getOrderId(),
        delivery.getOrderId().toString(),
        () -> new DeliveryPickUpEvent(delivery.getOrderId())
    );

    delivery.markAsPickUp();
  }

  @Transactional
  public void deliveryComplete(Long deliveryId){
    Delivery delivery = deliveryValidator.getByDeliveryId(deliveryId);

    outboxRecorder.record(
        EventType.DELIVERY_COMPLETE.name(),
        AggregateType.DELIVERY.name(),
        EventType.DELIVERY_COMPLETE.getKafkaTopic(),
        delivery.getOrderId(),
        delivery.getOrderId().toString(),
        () -> new DeliveryPickUpEvent(delivery.getOrderId())
    );

    delivery.markAsDeliverd();
  }

  @Transactional
  public void updateRiderStatus(RiderLocation riderLocation, Long riderId) {
    riderRedisService.updateRiderLastUpdateTime(riderId);
    riderRedisService.addRiderGeo(riderLocation, riderId);
  }
}
