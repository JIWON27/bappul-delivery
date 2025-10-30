package com.bappul.delivery.payment.web.v1.controller;

import com.bappul.delivery.payment.application.service.PaymentService;
import com.bappul.delivery.payment.web.v1.request.PaymentIntentRequest;
import com.bappul.delivery.payment.web.v1.request.PaymentVerifyRequest;
import com.bappul.delivery.payment.web.v1.request.PaymentWebhookRequest;
import com.bappul.delivery.payment.web.v1.response.PaymentIntentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/v1/payments")
public class PaymentController {

  private final PaymentService paymentService;

  @PostMapping("/intents")
  public ResponseEntity<ApiResponse<Void>> preparePayment(@RequestBody PaymentIntentRequest request) {
    paymentService.createIntent(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success());
  }

  @GetMapping("/intents/{orderId}")
  public ResponseEntity<ApiResponse<PaymentIntentResponse>> getPaymentIntent(@PathVariable Long orderId) {
    PaymentIntentResponse response = paymentService.getPaymentIntent(orderId);
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
  }

  // TODO /payment/complete로 바꿀지 말지 고민
  @PostMapping("/verify")
  public ResponseEntity<ApiResponse<Void>> paymentVerify(@RequestBody PaymentVerifyRequest request) {
    paymentService.paymentVerify(request);
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success());
  }

  @PostMapping("/webhook")
  public ResponseEntity<ApiResponse<Void>> paymentWebhook(@RequestBody PaymentWebhookRequest request) {
    paymentService.processWebhook(request);
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success());
  }
}
