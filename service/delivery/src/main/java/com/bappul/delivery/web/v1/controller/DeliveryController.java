package com.bappul.delivery.web.v1.controller;


import com.bappul.delivery.application.service.DeliveryService;
import com.bappul.delivery.web.v1.request.RiderLocation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/deliveries")
public class DeliveryController {

  private final DeliveryService deliveryService;

  @PostMapping("/{deliveryId}/accept")
  public ResponseEntity<ApiResponse<Void>> deliveryAccept(
      @PathVariable Long deliveryId,
      @AuthenticationPrincipal(expression = "claims['uid']") String riderId)
  {
    deliveryService.deliveryAccept(deliveryId, Long.valueOf(riderId));
    return  ResponseEntity.ok(ApiResponse.success());
  }

  @PostMapping("/{deliveryId}/pickup")
  public ResponseEntity<ApiResponse<Void>> deliveryPickUp(
      @PathVariable Long deliveryId,
      @AuthenticationPrincipal(expression = "claims['uid']") String riderId)
  {
    deliveryService.deliveryPickUp(deliveryId, Long.valueOf(riderId));
    return  ResponseEntity.ok(ApiResponse.success());
  }

  @PostMapping("/{deliveryId}/complete")
  public ResponseEntity<ApiResponse<Void>> deliveryComplete(@PathVariable Long deliveryId){
    deliveryService.deliveryComplete(deliveryId);
    return ResponseEntity.ok(ApiResponse.success());
  }

  @PutMapping
  public ResponseEntity<ApiResponse<Void>> riderStatus(
      @Valid @RequestBody RiderLocation riderLocation,
      @AuthenticationPrincipal(expression = "claims['uid']") String riderId)
  {
    deliveryService.updateRiderStatus(riderLocation, Long.valueOf(riderId));
    return  ResponseEntity.ok(ApiResponse.success());
  }
}
