package com.moduda.delivery.order.web.v1.controller;

import com.moduda.delivery.order.application.service.OrderService;
import com.moduda.delivery.order.web.v1.request.OrderRequest;
import com.moduda.delivery.order.web.v1.response.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {

  private final OrderService orderService;

  @PostMapping
  public ResponseEntity<ApiResponse<OrderResponse>> order(
      @RequestBody OrderRequest request,
      @AuthenticationPrincipal(expression = "claims['uid']") String userId)
  {
    OrderResponse orderResponse = orderService.createOrder(request, Long.valueOf(userId));
    return ResponseEntity.ok(ApiResponse.success(orderResponse));
  }

  @PostMapping("/{orderId}/cancel")
  public ResponseEntity<ApiResponse<Void>> cancel(
      @PathVariable Long orderId,
      @AuthenticationPrincipal(expression = "claims['uid']") String userId)
  {
    orderService.cancel(orderId, Long.valueOf(userId));
    return ResponseEntity.ok(ApiResponse.success());
  }
}
