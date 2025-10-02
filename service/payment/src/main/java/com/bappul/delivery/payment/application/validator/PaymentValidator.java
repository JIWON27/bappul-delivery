package com.bappul.delivery.payment.application.validator;

import static com.bappul.delivery.payment.exception.ServiceExceptionCode.NOT_FOUND_PAYMENT;

import com.bappul.delivery.payment.domain.entity.Payment;
import com.bappul.delivery.payment.domain.repository.PaymentRepository;
import exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentValidator {

  private final PaymentRepository paymentRepository;

  public Payment getPaymentByMerchantUid(String merchantUid) {
    return paymentRepository.findByMerchantUid(merchantUid)
        .orElseThrow(() -> new ServiceException(NOT_FOUND_PAYMENT));
  }

  public Payment getPaymentByOrderId(Long orderId) {
    return paymentRepository.findByOrderId(orderId)
        .orElseThrow(() -> new ServiceException(NOT_FOUND_PAYMENT));
  }
}
