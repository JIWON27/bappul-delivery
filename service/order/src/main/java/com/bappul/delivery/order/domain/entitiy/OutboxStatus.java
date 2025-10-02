package com.bappul.delivery.order.domain.entitiy;

public enum OutboxStatus {
  PENDING,
  SEND_SUCCESS,
  SEND_FAILED,
}
