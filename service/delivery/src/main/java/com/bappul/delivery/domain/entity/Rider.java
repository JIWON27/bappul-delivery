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
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "rider")
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE rider SET withdrawn_at = NOW() WHERE id = ?")
@SQLRestriction("withdrawn_at IS NULL")
public class Rider {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(name = "name", nullable = false)
  String name;

  @Column(name = "phone", nullable = false)
  String phone;

  @Column(name = "home_dong_code", nullable = false)
  String homeDongCode; // 행정동 코드

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  RiderStatus status;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  LocalDateTime updatedAt;

  @Column(name = "withdrawn_at")
  LocalDateTime withdrawnAt;

  @Builder
  public Rider(Long id, String name, String phone, String homeDongCode, RiderStatus status) {
    this.id = id;
    this.name = name;
    this.phone = phone;
    this.homeDongCode = homeDongCode;
    this.status = status;
  }
}
