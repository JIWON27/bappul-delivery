package com.moduda.delivery.catalog.web.v1.controller.store;

import com.moduda.delivery.catalog.application.service.MenuService;
import com.moduda.delivery.catalog.web.v1.response.menu.MenuResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
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

  /**
   * 가게 공개 API
   * [GET] 가게 메뉴 조회
   */

  private final MenuService menuService;

  @GetMapping("/{storeId}")
  public ResponseEntity<ApiResponse<List<MenuResponse>>> getMenus(@PathVariable Long storeId) {
    List<MenuResponse> responses = menuService.getMenus(storeId);
    return ResponseEntity.status(200).body(ApiResponse.success(responses));
  }
}
