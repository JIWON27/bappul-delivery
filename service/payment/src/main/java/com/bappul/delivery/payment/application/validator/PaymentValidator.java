package com.bappul.delivery.payment.application.validator;

import static com.bappul.delivery.payment.exception.ServiceExceptionCode.NOT_FOUND_PAYMENT;
import static com.bappul.delivery.payment.exception.ServiceExceptionCode.NOT_FOUND_PAYMENT_INTENT;

import com.bappul.delivery.payment.domain.entity.Payment;
import com.bappul.delivery.payment.domain.entity.PaymentIntent;
import com.bappul.delivery.payment.domain.repository.PaymentIntentRepository;
import com.bappul.delivery.payment.domain.repository.PaymentRepository;
import exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentValidator {

  private final PaymentRepository paymentRepository;
  private final PaymentIntentRepository paymentIntentRepository;

  public Payment getPaymentByOrderId(Long orderId) {
    return paymentRepository.findByOrderId(orderId)
        .orElseThrow(() -> new ServiceException(NOT_FOUND_PAYMENT));
  }

  public PaymentIntent getPaymentIntentByOrderId(Long orderId) {
    return paymentIntentRepository.findByOrderId(orderId)
        .orElseThrow(() -> new ServiceException(NOT_FOUND_PAYMENT_INTENT));
  }

  public PaymentIntent getPaymentIntentByPaymentId(String paymentId) {
    return paymentIntentRepository.findByPaymentId(paymentId)
        .orElseThrow(() -> new ServiceException(NOT_FOUND_PAYMENT_INTENT));
  }

}
