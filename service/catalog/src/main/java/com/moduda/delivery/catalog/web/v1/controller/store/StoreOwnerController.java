package com.moduda.delivery.catalog.web.v1.controller.store;

import com.moduda.delivery.catalog.application.service.MenuService;
import com.moduda.delivery.catalog.application.service.StoreService;
import com.moduda.delivery.catalog.web.v1.request.menu.MenuRequest;
import com.moduda.delivery.catalog.web.v1.request.menu.OptionRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/owner/stores")
public class StoreOwnerController {

  /**
   * 가게 점주 API
   * [POST] 메뉴 등록
   * [PATCH] 가게 영업 상태 관리 API
   */

  private final StoreService storeService;
  private final MenuService menuService;

  @PostMapping("/{storeId}/menus")
  public ResponseEntity<ApiResponse<Void>>  enroll(
      @PathVariable Long storeId,
      @Valid @RequestPart("menu") MenuRequest menuRequest,
      @Valid @RequestPart("option") OptionRequest optionRequest,
      @RequestPart("image") MultipartFile image,
      @AuthenticationPrincipal(expression = "claims['uid']") String userId)
  {
    menuService.enroll(storeId, menuRequest, optionRequest, image, Long.valueOf(userId));
    return ResponseEntity.status(200).body(ApiResponse.success());
  }

  @PatchMapping("/{storeId}")
  public ResponseEntity<ApiResponse<Void>> toggleStoreOpenStatus(
      @PathVariable Long storeId,
      @RequestParam("status") boolean status)
  {
    storeService.toggleStoreOpenStatus(storeId, status);
    return ResponseEntity.status(200).body(ApiResponse.success());
  }
}
