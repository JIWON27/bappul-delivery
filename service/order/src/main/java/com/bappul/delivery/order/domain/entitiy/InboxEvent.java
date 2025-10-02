package com.bappul.delivery.order.domain.entitiy;

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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Table(name = "inbox_events")
@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InboxEvent {

  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(name = "event_id", nullable = false)
  String eventId;

  @Column(name = "event_type", nullable = false, length = 128)
  String eventType;

  @Lob
  @Column(columnDefinition = "TEXT", nullable = false)
  String payload;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  InboxStatus status;

  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  LocalDateTime createdAt;

  @Builder
  public InboxEvent(String eventId, String eventType, String payload, InboxStatus status) {
    this.eventId = eventId;
    this.eventType = eventType;
    this.payload = payload;
    this.status = status;
  }

  public void markAsProcessed() {
    this.status = InboxStatus.PROCESSED;
  }

  public void markAsFailed() {
    this.status = InboxStatus.FAILED;
  }
}
