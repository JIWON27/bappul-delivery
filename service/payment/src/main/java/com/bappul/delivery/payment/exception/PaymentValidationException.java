package com.bappul.delivery.payment.exception;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PaymentValidationException extends RuntimeException {
  public enum Reason { MERCHANT_UID_MISMATCH, IMP_UID_MISMATCH,  AMOUNT_MISMATCH, NOT_PAID }

  private final Reason reason;
  private final String impUid;
  private final String merchantUid;
  private final BigDecimal price;
  private final Long orderId;
  private final Long paymentId;

  @Builder
  public PaymentValidationException(
      Reason reason, String message,
      String impUid, String merchantUid,
      BigDecimal price, Long orderId, Long paymentId) {
    super(message);
    this.reason = reason;
    this.impUid = impUid;
    this.merchantUid = merchantUid;
    this.price = price;
    this.orderId = orderId;
    this.paymentId = paymentId;
  }
}
