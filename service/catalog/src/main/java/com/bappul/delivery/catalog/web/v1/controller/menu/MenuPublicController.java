package com.bappul.delivery.catalog.web.v1.controller.menu;

import com.bappul.delivery.catalog.application.service.MenuService;
import com.bappul.delivery.catalog.web.v1.request.menu.internal.PricingInternalRequest;
import com.bappul.delivery.catalog.web.v1.response.menu.MenuResponse;
import com.bappul.delivery.catalog.web.v1.response.menu.internal.PricingInternalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/menus")
public class MenuPublicController {

  /**
   * 메뉴 공개 API
   * [GET] 메뉴 상세 조회 API
   * [POST] 장바구니 아이템 계산 API
   */

  private final MenuService menuService;

  @GetMapping("/{menuId}")
  public ResponseEntity<ApiResponse<MenuResponse>> getMenu(@PathVariable Long menuId){
    MenuResponse response = menuService.getMenu(menuId);
    return ResponseEntity.status(200).body(ApiResponse.success(response));
  }

  @PostMapping("/price-snapshots")
  public PricingInternalResponse pricingSnapshot(@RequestBody PricingInternalRequest request) {
    return menuService.calculate(request);
  }
}
