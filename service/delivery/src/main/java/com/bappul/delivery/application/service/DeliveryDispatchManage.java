package com.bappul.delivery.application.service;

public interface DeliveryDispatchManage {
  boolean tryAcquire(Long deliveryId);
  void reset(Long deliveryId);

}
