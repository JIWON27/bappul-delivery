package com.bappul.delivery.catalog.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Immutable;

@Entity
@Table(name = "store_search_view")
@Immutable
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StoreSearchView {

  @Column(name = "store_id", insertable = false, updatable = false)
  Long storeId;

  @Column(name = "store_name", insertable = false, updatable = false)
  String storeName;

  @Column(name = "category_id", insertable = false, updatable = false)
  Long categoryId;

  @Column(name = "category_name", insertable = false, updatable = false)
  String categoryName;

  @Id
  @Column(name = "menu_id", insertable = false, updatable = false)
  Long menuId;

  @Column(name = "menu_name", insertable = false, updatable = false)
  String menuName;

  @Column(name = "min_order_price", insertable = false, updatable = false)
  BigDecimal minOrderPrice;

  @Column(name = "delivery_fee", insertable = false, updatable = false)
  BigDecimal deliveryFee;

  @Column(name = "open_status", insertable = false, updatable = false)
  Boolean openStatus;

  @Column(name = "store_created_at", insertable = false, updatable = false)
  LocalDateTime createdAt;
}
