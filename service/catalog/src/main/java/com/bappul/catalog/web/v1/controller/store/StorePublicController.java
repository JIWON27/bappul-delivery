package com.bappul.catalog.web.v1.controller.store;

import com.bappul.catalog.application.service.MenuService;
import com.bappul.catalog.application.service.StoreService;
import com.bappul.catalog.web.v1.response.menu.MenuSummaryResponse;
import com.bappul.catalog.web.v1.response.store.StoreLocationResponse;
import com.bappul.catalog.web.v1.response.store.StoreResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stores")
public class StorePublicController {

  private final MenuService menuService;
  private final StoreService storeService;

  @GetMapping("/{storeId}/menus")
  public ResponseEntity<ApiResponse<List<MenuSummaryResponse>>> getMenus(@PathVariable Long storeId) {
    List<MenuSummaryResponse> responses = menuService.getMenus(storeId);
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responses));
  }

  @GetMapping("/{storeId}")
  public ResponseEntity<ApiResponse<StoreResponse>> getStore(@PathVariable Long storeId) {
    StoreResponse responses = storeService.getStore(storeId);
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responses));
  }

  @GetMapping("/{storeId}/location")
  public StoreLocationResponse getStoreLocation(@PathVariable Long storeId) {
    return storeService.getStoreLocation(storeId);
  }
}
