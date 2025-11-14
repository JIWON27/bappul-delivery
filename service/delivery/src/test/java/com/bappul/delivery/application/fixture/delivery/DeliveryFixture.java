package com.bappul.delivery.application.fixture.delivery;

import com.bappul.delivery.domain.entity.Delivery;
import com.bappul.delivery.domain.entity.DeliveryStatus;

public class DeliveryFixture {

  public static Delivery createReadyDelivery () {
    return Delivery.builder()
        .orderId(1L)
        .riderUserId(null)
        .status(DeliveryStatus.REQUESTED)
        .build();
  }

}
