package com.bappul.delivery.payment.web.v1.controller;

import com.bappul.delivery.payment.application.service.PaymentService;
import com.bappul.delivery.payment.web.v1.request.PaymentCreateRequest;
import com.bappul.delivery.payment.web.v1.request.PaymentValidationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {

  private final PaymentService paymentService;

  // TODO PG 관련 로직 잠시 보류
 /* @PostMapping("/prepare")
  public ResponseEntity<ApiResponse<String>> preparePayment(
      @RequestBody PaymentCreateRequest request,
      @AuthenticationPrincipal(expression = "claims['uid']") String userId) {
    String merchantUid = paymentService.preparePayment(request, Long.valueOf(userId));
    return ResponseEntity.ok(ApiResponse.success(merchantUid));
  }

  @GetMapping("/validation")
  public ResponseEntity<ApiResponse<Void>> paymentValidation(@PathVariable PaymentValidationRequest request) {
    paymentService.validationPayment(request);
    return ResponseEntity.ok(ApiResponse.success());
  }*/

  @PostMapping("/fake/prepare")
  public String fakePreparePayment(
      @RequestBody PaymentCreateRequest request,
      @AuthenticationPrincipal(expression = "claims['uid']") String userId) {
    return paymentService.fakePreparePayment(request, Long.valueOf(userId));
  }

  @GetMapping("/fake/validation")
  public ResponseEntity<ApiResponse<Void>> fakePaymentValidation(
      @RequestBody PaymentValidationRequest request,
      @RequestParam("mode") String mode)
  {
    paymentService.fakeValidationPayment(request, mode);
    return ResponseEntity.ok(ApiResponse.success());
  }
}
