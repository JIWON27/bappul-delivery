package com.bappul.delivery.web.v1.request;

import com.bappul.delivery.domain.entity.RiderStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RiderLocation {
  RiderStatus status;
  double latitude;
  double longitude;
}
