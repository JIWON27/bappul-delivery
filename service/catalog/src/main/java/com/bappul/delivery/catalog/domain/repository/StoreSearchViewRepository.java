package com.bappul.delivery.catalog.domain.repository;

import static com.bappul.delivery.catalog.domain.entity.QStoreSearchView.storeSearchView;

import com.bappul.delivery.catalog.application.service.CursorCodec;
import com.bappul.delivery.catalog.application.service.CursorCodec.Cursor;
import com.bappul.delivery.catalog.domain.entity.QStoreSearchView;
import com.bappul.delivery.catalog.domain.entity.Store;
import com.bappul.delivery.catalog.web.v1.controller.store.StoreSearchCondition;
import com.bappul.delivery.catalog.web.v1.controller.store.StoreSort;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StoreSearchViewRepository {

  private final CursorCodec cursorCodec;
  private final JPAQueryFactory queryFactory;
  private final StoreRepository storeRepository;

  public StoreSearchResult findStoresWithCondition(StoreSearchCondition condition){
    int size = condition.getSize();

    StoreSort sort = condition.getSortOrDefault();

    CursorType<?> cursor = getCursor(sort, condition.getCursor());

    // 카테고리, 최소 주문 가격, 배달비 조건, 키워드 추가
    QStoreSearchView qStoreSearchView = storeSearchView;

    BooleanBuilder builder = new BooleanBuilder();
    if (condition.getCategoryId() != null){
      builder.and(qStoreSearchView.categoryId.eq(condition.getCategoryId()));
    }
    if (condition.getMinOrderPriceLte() != null) {
      builder.and(qStoreSearchView.minOrderPrice.loe(condition.getMinOrderPriceLte()));
    }
    if (condition.getDeliveryFeeLte() != null) {
      builder.and(qStoreSearchView.deliveryFee.loe(condition.getDeliveryFeeLte()));
    }
    if (condition.getKeyword() != null) {
      builder.and(qStoreSearchView.storeName.contains(condition.getKeyword())
          .or(qStoreSearchView.menuName.contains(condition.getKeyword()))
          .or(qStoreSearchView.categoryName.contains(condition.getKeyword())));
    }

    BooleanExpression cursorExpression = getCursorExpression(sort, cursor);
    List<Tuple> tuples = getTuple(builder, size, sort, cursorExpression);

    boolean hasNext = tuples.size() > size;
    String nextCursor = null;
    if (hasNext) {
      tuples = tuples.subList(0, size);
      Tuple last = tuples.get(tuples.size() - 1);
      nextCursor = generateNextCursor(condition.getSort(), last);
    }

    // storeIds 순서대로 stores 순서 정렬
    List<Long> storeIds = tuples.stream()
        .map(tuple -> tuple.get(qStoreSearchView.storeId))
        .toList();

    List<Store> stores = storeRepository.findAllByIdIn(storeIds);

    Map<Long, Store> storesMap = stores.stream()
        .collect(Collectors.toMap(Store::getId, store -> store));

    List<Store> sortedStores = storeIds.stream()
        .map(storesMap::get)
        .filter(Objects::nonNull)
        .toList();

    return new StoreSearchResult(sortedStores, hasNext, nextCursor);
  }

  private CursorType<?> getCursor(StoreSort sort, String cursor) {
    switch (sort) {
      case MIN_ORDER_PRICE: {
        Cursor<BigDecimal> bigDecimalCursor = cursorCodec.decodeMinOrderPrice(cursor);
        BigDecimal minOrderPrice = bigDecimalCursor.sortKey();
        return new CursorType<>(minOrderPrice, bigDecimalCursor.storeId());
      }
      case LATEST: {
        Cursor<LocalDateTime> localDateTimeCursor = cursorCodec.decodeLatest(cursor);
        LocalDateTime storeCreateAt = localDateTimeCursor.sortKey();
        return new CursorType<>(storeCreateAt, localDateTimeCursor.storeId());
      }
    }
    throw new IllegalArgumentException("Unsupported sort type: " + sort);
  }

  private String generateNextCursor(StoreSort sort, Tuple last) {
    switch (sort) {
      case MIN_ORDER_PRICE: {
        Long lastStoreId = last.get(storeSearchView.storeId);
        BigDecimal minOrderPrice = last.get(storeSearchView.minOrderPrice);
        return cursorCodec.encodeMinOrderPrice(minOrderPrice, lastStoreId);
      }
      case LATEST: {
        Long lastStoreId = last.get(storeSearchView.storeId);
        LocalDateTime lastCreatedAt = last.get(storeSearchView.createdAt);
        return cursorCodec.encodeLatest(lastCreatedAt, lastStoreId);
      }
    }
    throw new IllegalArgumentException("generateNextCursor fail : " + sort);
  }

  private List<Tuple> getTuple(BooleanBuilder builder, int size, StoreSort sort, BooleanExpression cursorPred) {
    return switch (sort) {
      case MIN_ORDER_PRICE ->
          queryFactory.select(storeSearchView.storeId, storeSearchView.minOrderPrice)
              .distinct()
              .from(storeSearchView)
              .where(builder.and(cursorPred))
              .orderBy(storeSearchView.minOrderPrice.asc(), storeSearchView.storeId.asc())
              .limit(size + 1L)
              .fetch();
      case LATEST -> queryFactory.select(storeSearchView.storeId, storeSearchView.createdAt)
          .distinct()
          .from(storeSearchView)
          .where(builder.and(cursorPred))
          .orderBy(storeSearchView.createdAt.desc(), storeSearchView.storeId.desc())
          .limit(size + 1L)
          .fetch();
    };
  }

  private BooleanExpression getCursorExpression(StoreSort sort, CursorType<?> cursorType) {
    BooleanExpression cursorExpression = Expressions.TRUE.isTrue();

    switch (sort) {
      case MIN_ORDER_PRICE: {
        BigDecimal minOrderPrice = (BigDecimal) cursorType.sortKey();
        Long storeId = cursorType.storeId();
        if (minOrderPrice != null && storeId != null) {
          cursorExpression = storeSearchView.minOrderPrice
              .gt(minOrderPrice)
              .or(storeSearchView.minOrderPrice.eq(minOrderPrice).and(storeSearchView.storeId.gt(storeId)));
        }
        return cursorExpression;
      }
      case LATEST: {
        LocalDateTime storeCreateAt = (LocalDateTime) cursorType.sortKey();
        Long storeId = cursorType.storeId();
        if (storeCreateAt != null && storeId != null) {
          cursorExpression = storeSearchView.createdAt.lt(storeCreateAt)
              .or(storeSearchView.createdAt.eq(storeCreateAt)
                  .and(storeSearchView.storeId.lt(storeId)));
        }
        return cursorExpression;
      }
    }
    throw new IllegalArgumentException("Unsupported sort type: " + sort);
  }

  public record CursorType<T>(T sortKey, Long storeId) {}
  public record StoreSearchResult(List<Store> content, boolean hasNext, String nextCursor) {}

}
