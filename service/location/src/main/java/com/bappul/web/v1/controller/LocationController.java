package com.bappul.web.v1.controller;

import com.bappul.application.service.LocationService;
import com.bappul.web.v1.request.KakaoAddressQuery;
import com.bappul.web.v1.response.LocationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/location")
public class LocationController {

  private final LocationService locationService;

  @GetMapping
  public LocationResponse getLocation(@ModelAttribute KakaoAddressQuery params) {
    return locationService.getLocation(params);
  }

}
