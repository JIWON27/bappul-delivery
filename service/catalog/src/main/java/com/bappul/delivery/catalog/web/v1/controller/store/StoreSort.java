package com.bappul.delivery.catalog.web.v1.controller.store;

import com.bappul.delivery.catalog.domain.entity.QStoreSearchView;
import com.querydsl.core.types.OrderSpecifier;
import lombok.Getter;

@Getter
public enum StoreSort {
  LATEST("최신순") {
    @Override
    public OrderSpecifier<?> getOrderSpecifier() {
      return QStoreSearchView.storeSearchView.createdAt.desc();
    }
  },
  MIN_ORDER_PRICE("최소 주문 금액순") {
    @Override
    public OrderSpecifier<?> getOrderSpecifier() {
      return QStoreSearchView.storeSearchView.minOrderPrice.asc();
    }
  }
  ;
  final String description;
  StoreSort(String description) {
    this.description = description;
  }

  public abstract OrderSpecifier<?> getOrderSpecifier();

}
