package com.moduda.delivery.catalog.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
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
@Table(name = "menu_option_values")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MenuOptionValue {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "menu_option_groups_id", nullable = false)
  MenuOptionGroup menuOptionGroup;

  @Column(name = "name", nullable = false, length = 50)
  String name;

  @Column(name = "additional_price", nullable = false, precision = 10, scale = 0)
  BigDecimal additionalPrice;

  @Column(name = "sold_out", nullable = false)
  Boolean soldOut = false;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  LocalDateTime updatedAt;

  @Builder
  public MenuOptionValue(MenuOptionGroup menuOptionGroup, String name, BigDecimal additionalPrice) {
    this.menuOptionGroup = menuOptionGroup;
    this.name = name;
    this.additionalPrice = additionalPrice;
  }

  public void updateSoldOut(Boolean soldOut) {
    this.soldOut = soldOut;
  }
}
