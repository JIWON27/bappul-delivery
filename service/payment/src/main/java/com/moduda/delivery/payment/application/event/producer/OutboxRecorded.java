package com.moduda.delivery.payment.application.event.producer;

import com.moduda.delivery.payment.application.event.contracts.common.EventType;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OutboxRecorded {
  UUID eventId;
  EventType eventType;
}
