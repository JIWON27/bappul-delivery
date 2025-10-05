package com.bappul.delivery.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "delivery")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Delivery {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(name = "order_id", nullable = false)
  Long orderId;

  @Column(name = "rider_user_id", nullable = false)
  Long riderUserId;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  DeliveryStatus status;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  LocalDateTime updatedAt;

  @Builder
  public Delivery(Long orderId, Long riderUserId, DeliveryStatus status) {
    this.orderId = orderId;
    this.riderUserId = riderUserId;
    this.status = status;
  }


  public void updateRiderUserId(Long riderUserId) {
    this.riderUserId = riderUserId;
  }

  public void markAsAssigned() {
    this.status = DeliveryStatus.ASSIGNED;
  }

  public void markAsPickUp() {
    this.status = DeliveryStatus.PICKED_UP;
  }

  public void markAsDelivered() {
    this.status = DeliveryStatus.DELIVERED;
  }

  public void markAsCancelled() {
    this.status = DeliveryStatus.CANCELLED;
  }

  public void markAsDeliverd() {
    this.status = DeliveryStatus.DELIVERED;
  }
}
