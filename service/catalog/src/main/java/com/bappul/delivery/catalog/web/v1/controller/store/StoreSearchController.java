package com.bappul.delivery.catalog.web.v1.controller.store;

import com.bappul.delivery.catalog.application.service.SearchService;
import com.bappul.delivery.catalog.web.v1.response.search.StoreSearchResponse;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import response.ApiResponse;
import response.CursorResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stores")
public class StoreSearchController {

  /**
   * 배달 플랫폼 검색
   * [GET] 가게 검색 API
   * 정렬 조건 - 최소 주문 가격이 낮은 순, 최신 순, (추가예정)
   */

  private final SearchService searchService;

  @GetMapping("/search")
  public ResponseEntity<ApiResponse<CursorResponse<StoreSearchResponse>>> getStores(
      @RequestParam(required = false) String cursor,
      @RequestParam(required = false) Long categoryId,
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) BigDecimal minOrderPriceLte,
      @RequestParam(required = false) BigDecimal deliveryFeeLte,
      @RequestParam(defaultValue = "LATEST") StoreSort sort,
      @RequestParam(defaultValue = "30") int size)
  {
    StoreSearchCondition condition = StoreSearchCondition.builder()
        .cursor(cursor)
        .categoryId(categoryId)
        .keyword(keyword)
        .minOrderPriceLte(minOrderPriceLte)
        .deliveryFeeLte(deliveryFeeLte)
        .sort(sort)
        .size(size)
        .build();
    CursorResponse<StoreSearchResponse> response = searchService.search(condition);
    return ResponseEntity.status(200).body(ApiResponse.success(response));
  }

}
