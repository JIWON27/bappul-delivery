package com.moduda.delivery.payment.web.v1.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentValidationRequest {
  String impUid;
  String merchantUid;
}
