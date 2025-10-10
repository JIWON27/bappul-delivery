package com.bappul.delivery.domain.entity;

public enum OutboxStatus {
  PENDING,
  SEND_SUCCESS,
  SEND_FAILED,
}
