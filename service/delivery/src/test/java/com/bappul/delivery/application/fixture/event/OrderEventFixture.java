package com.bappul.delivery.application.fixture.event;

import com.bappul.delivery.application.event.contracts.order.OrderReadyEvent;

public class OrderEventFixture {

  public static OrderReadyEvent readyEvent() {
    return OrderReadyEvent.builder()
        .orderId(1L)
        .adminCode("adminCode")
        .legalCode("legalCode")
        .latitude(37.5)
        .longitude(127.03)
        .build();
  }

}
