package com.bappul.delivery.catalog.web.v1.controller.menu;

import com.bappul.delivery.catalog.application.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/owner/menus")
public class MenuOwnerController {

  /**
   * 메뉴 점주 API
   * [DELETE] 메뉴 삭제 API
   * [PATCH] 메뉴 품절 토글 API
   * [PATCH] 메뉴 특정 옵션 품절 토글 API
   */

  private final MenuService menuService;

  @DeleteMapping("/{menuId}")
  public ResponseEntity<ApiResponse<Void>> deleteById(@PathVariable Long menuId) {
    menuService.deleteById(menuId);
    return ResponseEntity.status(200).body(ApiResponse.success());
  }

  @PatchMapping("/{menuId}")
  public ResponseEntity<ApiResponse<Void>> toggleMenuSoldOut(
      @PathVariable Long menuId,
      @RequestParam("soldOut") boolean soldOut,
      @AuthenticationPrincipal(expression = "claims['uid']") String ownerId)
  {
    menuService.toggleMenuSoldOut(menuId, soldOut, Long.valueOf(ownerId));
    return ResponseEntity.status(200).body(ApiResponse.success());
  }

  @PatchMapping("/options/{menuOptionValueId}")
  public ResponseEntity<ApiResponse<Void>> toggleOptionStatus(
      @PathVariable Long menuOptionValueId,
      @RequestParam("soldOut") boolean soldOut,
      @AuthenticationPrincipal(expression = "claims['uid']") String ownerId)
  {
    menuService.toggleOptionStatus(menuOptionValueId, soldOut, Long.valueOf(ownerId));
    return ResponseEntity.status(200).body(ApiResponse.success());
  }
}
