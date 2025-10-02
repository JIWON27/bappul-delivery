package com.moduda.delivery.pomotion.web.v1.controller;

import com.moduda.delivery.pomotion.application.service.CouponService;
import com.moduda.delivery.pomotion.application.service.FirstComeCouponIssueService;
import com.moduda.delivery.pomotion.web.v1.request.internal.CouponDiscountCalculateRequest;
import com.moduda.delivery.pomotion.web.v1.response.CouponResponse;
import com.moduda.delivery.pomotion.web.v1.response.internal.CouponDiscountCalculateResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/coupons")
public class CouponController {

  private final CouponService couponService;
  private final FirstComeCouponIssueService firstComeCouponIssueService;

  // 쿠폰 코드 등록
  @PostMapping("/issuance")
  public ResponseEntity<ApiResponse<Void>> registerCoupon(
      @RequestParam("code") String code,
      @AuthenticationPrincipal(expression = "claims['uid']") String userId) {
    couponService.registerOfflineCouponCode(code, Long.valueOf(userId));
    return ResponseEntity.ok(ApiResponse.success());
  }

  // 쿠폰 발급
  @PostMapping("/policies/{couponPolicyId}")
  public ResponseEntity<ApiResponse<Void>> issueCoupon(
      @PathVariable Long couponPolicyId,
      @AuthenticationPrincipal(expression = "claims['uid']") String userId) {
    couponService.issueCoupon(couponPolicyId, Long.valueOf(userId));
    return ResponseEntity.ok(ApiResponse.success());
  }

  @GetMapping("/{couponId}")
  public ResponseEntity<ApiResponse<CouponResponse>> getCoupon(@PathVariable Long couponId) {
    CouponResponse response = couponService.getCoupon(couponId);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  // 내 쿠폰함 조회
  @GetMapping
  public ResponseEntity<ApiResponse<List<CouponResponse>>> getCoupons(@AuthenticationPrincipal(expression = "claims['uid']") String userId) {
    List<CouponResponse> response = couponService.getCoupons(Long.valueOf(userId));
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  //선착순 쿠폰
  @PostMapping("/policies/{couponPolicyId}/firstcome")
  public ResponseEntity<ApiResponse<Void>> firstComeCoupon(
      @PathVariable Long couponPolicyId,
      @AuthenticationPrincipal(expression = "claims['uid']") String userId)
  {
    firstComeCouponIssueService.firstComeCouponIssue(couponPolicyId, Long.valueOf(userId));
    return ResponseEntity.ok(ApiResponse.success());
  }

  @PostMapping("/discount")
  public CouponDiscountCalculateResponse calculate(
      @RequestBody CouponDiscountCalculateRequest request,
      @AuthenticationPrincipal(expression = "claims['uid']") String userId)
  {
    CouponDiscountCalculateResponse response = couponService.calculateDiscount(request);
    return response;
  }
}
