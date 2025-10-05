package com.bappul.delivery.order.web.v1.controller;

import com.bappul.delivery.order.application.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stores/{storeId}/orders")
public class OrderOwnerController {

  private final OrderService orderService;

  @PostMapping("/{orderId}/accept")
  public  ResponseEntity<ApiResponse<Void>> accept(
      @PathVariable Long storeId,
      @PathVariable Long orderId,
      @AuthenticationPrincipal(expression = "claims['uid']") String userId)
  {
    orderService.accept(storeId, orderId, Long.valueOf(userId));
    return ResponseEntity.ok(ApiResponse.success());
  }

  @PostMapping("/{orderId}/reject")
  public  ResponseEntity<ApiResponse<Void>> reject(
      @PathVariable Long storeId,
      @PathVariable Long orderId,
      @AuthenticationPrincipal(expression = "claims['uid']") String userId)
  {
    orderService.reject(storeId, orderId, Long.valueOf(userId));
    return ResponseEntity.ok(ApiResponse.success());
  }

  @PostMapping("/{orderId}/ready")
  public  ResponseEntity<ApiResponse<Void>> ready(@PathVariable Long storeId, @PathVariable Long orderId) {
    orderService.ready(storeId, orderId);
    return ResponseEntity.ok(ApiResponse.success());
  }
}

