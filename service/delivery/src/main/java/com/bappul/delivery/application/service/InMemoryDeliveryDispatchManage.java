package com.bappul.delivery.application.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class InMemoryDeliveryDispatchManage implements DeliveryDispatchManage {

  private static final Map<Long, Boolean> deliveryDispatchMap = new ConcurrentHashMap<>();

  @Override
  public boolean tryAcquire(Long deliveryId) {
    return deliveryDispatchMap.putIfAbsent(deliveryId, Boolean.TRUE) == null;
  }

  @Override
  public void reset(Long deliveryId) {
    deliveryDispatchMap.remove(deliveryId);
  }
}
