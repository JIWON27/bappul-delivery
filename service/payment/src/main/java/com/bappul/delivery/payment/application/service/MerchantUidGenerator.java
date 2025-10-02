package com.bappul.delivery.payment.application.service;

import static com.bappul.delivery.payment.exception.ServiceExceptionCode.INVALID_ARGUMENT_ORDER_ID;

import exception.ServiceException;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class MerchantUidGenerator {

  public String generate(Long orderId) {
    if (Objects.isNull(orderId) || orderId <= 0) {
      throw new ServiceException(INVALID_ARGUMENT_ORDER_ID);
    }
    return "order-%d-%s".formatted(orderId, UUID.randomUUID());
  }
}
