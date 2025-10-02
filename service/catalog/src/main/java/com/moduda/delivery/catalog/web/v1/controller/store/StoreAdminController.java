package com.moduda.delivery.catalog.web.v1.controller.store;

import com.moduda.delivery.catalog.application.service.StoreService;
import com.moduda.delivery.catalog.web.v1.request.store.StoreRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/stores")
public class StoreAdminController {

  /**
   * 가게 관리자 API
   * [POST] 가게 등록
   * [DELETE] 가게 삭제
   */

  private final StoreService storeService;

  @PostMapping
  public ResponseEntity<ApiResponse<Void>> enroll(
      @Valid @RequestBody StoreRequest request,
      @AuthenticationPrincipal(expression = "claims['uid']") String userId)
  {
    storeService.enroll(request, Long.valueOf(userId));
    return ResponseEntity.status(200).body(ApiResponse.success());
  }

  @DeleteMapping("/{storeId}")
  public ResponseEntity<ApiResponse<Void>> deleteById(@PathVariable Long storeId) {
    storeService.deleteById(storeId);
    return ResponseEntity.status(200).body(ApiResponse.success());
  }

}
