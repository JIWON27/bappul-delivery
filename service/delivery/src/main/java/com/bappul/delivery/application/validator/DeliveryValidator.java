package com.bappul.delivery.application.validator;

import static com.bappul.delivery.exception.ServiceExceptionCode.NOT_FOUND_DELIVERY;

import com.bappul.delivery.domain.entity.Delivery;
import com.bappul.delivery.domain.entity.DeliveryStatus;
import com.bappul.delivery.domain.repository.DeliveryRepository;
import exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeliveryValidator {

  private final DeliveryRepository deliveryRepository;

  public Delivery getByOrderId(Long orderId) {
    return deliveryRepository.findByOrderId(orderId)
        .orElseThrow(() -> new ServiceException(NOT_FOUND_DELIVERY));
  }

  public Delivery getByDeliveryId(Long deliveryId) {
    return deliveryRepository.findById(deliveryId)
        .orElseThrow(() -> new ServiceException(NOT_FOUND_DELIVERY));
  }

  public int updateDelivery(Long deliveryId, Long riderUserId) {
    return deliveryRepository.tryAssign(deliveryId, riderUserId, DeliveryStatus.ASSIGNED);
  }

}
