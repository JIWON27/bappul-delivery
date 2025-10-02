package com.moduda.delivery.payment.domain.entity;

import com.moduda.delivery.payment.application.event.contracts.common.AggregateType;
import com.moduda.delivery.payment.application.event.contracts.common.EventType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Table(name = "outbox_events")
@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OutBoxEvent {

  @Column(name = "id", nullable = false)
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(name = "event_id", nullable = false, unique = true)
  UUID eventId;

  @Enumerated(EnumType.STRING)
  @Column(name = "event_type", nullable = false, length = 128)
  EventType eventType;

  @Column(name = "aggregate_id")
  Long aggregateId; // 이벤트가 소속된 집합 ID

  @Enumerated(EnumType.STRING)
  @Column(name = "aggregate_type", length = 30)
  AggregateType aggregateType; // 집합 종류 ex) Order, Payment, CouponPolicy

  @Column(name = "status", nullable = false, length = 20)
  OutboxStatus status;

  @Column(name = "partition_key", nullable = false, length = 128)
  String partitionKey; // 카프카 키. 순서 보장이 필요한 경계(동일 주문/상품) 단위로”.

  @Lob
  @Column(columnDefinition = "TEXT", nullable = false)
  String payload;

  @Column(name = "occurred_at")
  LocalDateTime occurredAt;

  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  LocalDateTime createdAt;

  @Column(name = "processed_at")
  LocalDateTime processedAt;

  @Builder
  public OutBoxEvent(UUID eventId, EventType eventType, Long aggregateId, AggregateType aggregateType,
      String partitionKey, OutboxStatus status, String payload, LocalDateTime occurredAt) {
    this.eventId = eventId;
    this.eventType = eventType;
    this.aggregateId = aggregateId;
    this.aggregateType = aggregateType;
    this.status = status;
    this.partitionKey = partitionKey;
    this.payload = payload;
    this.occurredAt = occurredAt;
  }

  public void changeStatus(OutboxStatus status) {
    this.status = status;
    this.processedAt = LocalDateTime.now();
  }

}
