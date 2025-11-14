package com.bappul.delivery.application.service;

import com.bappul.delivery.application.event.contracts.common.AggregateType;
import com.bappul.delivery.application.event.contracts.common.EventType;
import com.bappul.delivery.application.event.contracts.delivery.DeliveryPickUpEvent;
import com.bappul.delivery.application.validator.DeliveryValidator;
import com.bappul.delivery.domain.entity.Delivery;
import com.bappul.delivery.exception.ServiceExceptionCode;
import com.bappul.delivery.web.v1.request.RiderLocation;
import com.bappul.event.outbox.OutboxRecorder;
import exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeliveryService {

  private final DeliveryValidator deliveryValidator;
  private final OutboxRecorder outboxRecorder;
  private final RiderRedisService riderRedisService;
  private final DeliveryDispatchManage deliveryDispatchManage;

  @Transactional
  public void deliveryAccept(Long deliveryId, Long riderUserId){
    if (!deliveryDispatchManage.tryAcquire(deliveryId)) {
      throw new ServiceException(ServiceExceptionCode.ALREADY_PROCESSING_OTHER_RIDER);
    }

    boolean isSuccess = false;
    try {
      int updateRow = deliveryValidator.updateDelivery(deliveryId, riderUserId);
      if (updateRow != 1) {
        throw new ServiceException(ServiceExceptionCode.ALREADY_PROCESSED_OTHER_RIDER);
      }
      isSuccess = true;
    } finally {
      if (!isSuccess) {
        deliveryDispatchManage.reset(deliveryId);
      }
    }
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
    deliveryDispatchManage.reset(deliveryId);
  }

  @Transactional
  public void updateRiderStatus(RiderLocation riderLocation, Long riderId) {
    riderRedisService.updateRiderLastUpdateTime(riderId);
    riderRedisService.addRiderGeo(riderLocation, riderId);
  }
}
