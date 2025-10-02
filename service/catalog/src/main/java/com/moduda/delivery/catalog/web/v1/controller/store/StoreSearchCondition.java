package com.moduda.delivery.catalog.web.v1.controller.store;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class StoreSearchCondition {
  Long categoryId;
  String keyword;
  BigDecimal minOrderPriceLte;
  BigDecimal deliveryFeeLte;
  StoreSort sort ;
  String cursor;
  int size;

  public StoreSort getSortOrDefault() {
    return sort != null ? sort : StoreSort.LATEST;
  }
}
