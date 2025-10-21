package com.bappul.event.outbox;

public enum OutboxStatus {
  PENDING,
  SEND_SUCCESS,
  SEND_FAILED,
}
