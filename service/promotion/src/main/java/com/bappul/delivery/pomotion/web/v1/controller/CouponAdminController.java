package com.bappul.delivery.pomotion.web.v1.controller;

import com.bappul.delivery.pomotion.application.service.CouponService;
import com.bappul.delivery.pomotion.web.v1.request.CouponCreateRequest;
import com.bappul.delivery.pomotion.web.v1.request.CouponPolicyRequest;
import com.bappul.delivery.pomotion.web.v1.response.CouponPolicyResponse;
import jakarta.validation.Valid;
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
@RequestMapping("/api/v1/admin/coupons")
public class CouponAdminController {
  private final CouponService couponService;

  @PostMapping("/policies")
  public ResponseEntity<ApiResponse<Void>> createCouponPolicy(@RequestBody @Valid CouponPolicyRequest request) {
    couponService.createCouponPolicy(request);
    return ResponseEntity.status(200).body(ApiResponse.success());
  }

  @GetMapping("/policies/{couponPolicyId}")
  public ResponseEntity<ApiResponse<CouponPolicyResponse>> getCouponPolicy(@PathVariable Long couponPolicyId) {
    CouponPolicyResponse response = couponService.getCouponPolicy(couponPolicyId);
    return ResponseEntity.status(200).body(ApiResponse.success(response));
  }

  @PostMapping
  public ResponseEntity<ApiResponse<Void>> preIssueCoupons(@RequestBody @Valid CouponCreateRequest request) {
    couponService.preIssueCoupons(request);
    return  ResponseEntity.status(200).body(ApiResponse.success());
  }
}
