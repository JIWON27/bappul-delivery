package com.bappul.catalog.web.v1.controller.store;

import com.bappul.catalog.application.service.SearchService;
import com.bappul.catalog.domain.repository.StoreSort;
import com.bappul.catalog.web.v1.request.store.StoreSearchCondition;
import com.bappul.catalog.web.v1.response.search.StoreSearchResponse;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
  }

}
