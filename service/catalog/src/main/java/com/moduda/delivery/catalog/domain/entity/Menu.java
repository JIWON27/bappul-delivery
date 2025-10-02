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
@Table(name = "menus")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Menu {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "store_id", nullable = false)
  Store store;

  @Column(name = "name",  nullable = false, length = 50)
  String name;

  @Column(name = "photo_url", nullable = false, length = 500)
  String photoUrl;

  @Column(name = "price",  nullable = false, precision = 10, scale = 0)
  BigDecimal price;

  @Column(name = "description", length = 255)
  String description;

  @Column(name = "visible", nullable = false)
  Boolean visible = true;

  @Column(name = "sold_out", nullable = false)
  Boolean soldOut = false;

  @Column(name = "sort_order", nullable = false)
  Integer sortOrder = 0;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  LocalDateTime updatedAt;

  @Builder
  public Menu(Store store, String name, String photoUrl, BigDecimal price, String description,
      Integer sortOrder) {
    this.store = store;
    this.name = name;
    this.photoUrl = photoUrl;
    this.price = price;
    this.description = description;
    this.sortOrder = sortOrder;
  }

  public void updateVisible(Boolean visible) {
    this.visible = visible;
  }

  public void updateSoldOut(Boolean soldOut) {
    this.soldOut = soldOut;
  }
}
