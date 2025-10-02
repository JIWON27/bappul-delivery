package com.moduda.delivery.order.domain.entitiy;

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

@Table(name = "order_line_option")
@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderLineOption {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_line_id")
  OrderLine orderLine;

  @Column(name = "option_value_id")
  Long optionValueId;

  @Column(name = "option_name")
  String optionName;

  @Column(name = "option_price")
  BigDecimal optionPrice; // 옵션 단품 값

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  LocalDateTime updatedAt;

  @Builder
  public OrderLineOption(OrderLine orderLine, Long optionValueId, String optionName,
      BigDecimal optionPrice) {
    this.orderLine = orderLine;
    this.optionValueId = optionValueId;
    this.optionName = optionName;
    this.optionPrice = optionPrice;
  }
}
