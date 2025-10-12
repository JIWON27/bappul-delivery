package com.bappul.catalog.web.v1.controller.store;

import com.bappul.catalog.application.service.MenuService;
import com.bappul.catalog.application.service.StoreService;
import com.bappul.catalog.web.v1.request.menu.MenuRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/owner/stores")
public class StoreOwnerController {

  private final StoreService storeService;
  private final MenuService menuService;

  @PostMapping("/{storeId}/menus")
  public ResponseEntity<ApiResponse<Void>>  enroll(
      @PathVariable Long storeId,
      @Valid @RequestBody MenuRequest menuRequest,
      @AuthenticationPrincipal(expression = "claims['uid']") String userId)
  {
    menuService.enroll(storeId, menuRequest, Long.valueOf(userId));
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success());
  }

  @PatchMapping("/{storeId}")
  public ResponseEntity<ApiResponse<Void>> setStoreOpenStatus(
      @PathVariable Long storeId,
      @RequestParam("status") boolean status)
  {
    storeService.setStoreOpenStatus(storeId, status);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
