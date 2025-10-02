package com.moduda.delivery.cart.web.v1.controller;

import com.moduda.delivery.cart.application.service.CartService;
import com.moduda.delivery.cart.web.v1.request.CartRequest;
import com.moduda.delivery.cart.web.v1.response.CartResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/carts")
public class CartController {

  private final CartService cartService;

  @PostMapping
  public ResponseEntity<ApiResponse<Void>> addCartItem(
      @RequestBody @Valid CartRequest request,
      @AuthenticationPrincipal(expression = "claims['uid']") String userId)
  {
    cartService.addCartItem(request, Long.valueOf(userId));
    return ResponseEntity.status(200).body(ApiResponse.success());
  }

  @GetMapping
  public ResponseEntity<ApiResponse<CartResponse>> getCartItems(@AuthenticationPrincipal(expression = "claims['uid']") String userId) {
    CartResponse response = cartService.getCartItems(Long.valueOf(userId));
    return ResponseEntity.status(200).body(ApiResponse.success(response));
  }

}
