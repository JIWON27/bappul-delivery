package com.bappul.delivery.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "rider_id", nullable = true)
  Rider rider;

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
  public Delivery(Long orderId, Rider rider, DeliveryStatus status) {
    this.orderId = orderId;
    this.rider = rider;
    this.status = status;
  }
}
