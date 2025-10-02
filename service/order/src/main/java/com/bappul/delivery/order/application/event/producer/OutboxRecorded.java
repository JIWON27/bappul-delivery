package com.bappul.delivery.order.application.event.producer;

import com.bappul.delivery.order.application.event.contracts.common.EventType;
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
