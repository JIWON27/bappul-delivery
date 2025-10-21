package com.bappul.delivery.payment.application.service;

import com.bappul.delivery.payment.application.event.contracts.common.AggregateType;
import com.bappul.delivery.payment.application.event.contracts.common.EventType;
import com.bappul.delivery.payment.application.event.contracts.payment.PaymentFailEvent;
import com.bappul.delivery.payment.application.event.contracts.payment.PaymentSuccessEvent;
import com.bappul.delivery.payment.application.validator.PaymentValidator;
import com.bappul.delivery.payment.domain.entity.Payment;
import com.bappul.delivery.payment.domain.entity.PaymentStatus;
import com.bappul.delivery.payment.domain.repository.PaymentRepository;
import com.bappul.delivery.payment.web.v1.request.PaymentCreateRequest;
import com.bappul.delivery.payment.web.v1.request.PaymentValidationRequest;
import com.bappul.event.outbox.OutboxRecorder;
import com.siot.IamportRestClient.IamportClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

  private final IamportClient iamportClient;

  private final OutboxRecorder outboxRecorder;
  private final PaymentRepository paymentRepository;

  private final PaymentValidator paymentValidator;
  private final MerchantUidGenerator merchantUidGenerator;

  @Transactional
  public String fakePreparePayment(PaymentCreateRequest request, Long userId) {
    String merchantUid = merchantUidGenerator.generate(request.getOrderId());
    Payment payment = Payment.builder()
        .orderId(request.getOrderId())
        .userId(userId)
        .status(PaymentStatus.PENDING)
        .impUid(null)
        .merchantUid(merchantUid)
        .price(request.getPayablePrice())
        .build();

    paymentRepository.save(payment);
    return merchantUid;
  }

  @Transactional
  public void fakeValidationPayment(PaymentValidationRequest request, String mode) {
    String impUid = request.getImpUid();
    String merchantUid = request.getMerchantUid();

    Payment payment = paymentValidator.getPaymentByMerchantUid(merchantUid);

    if (payment.getStatus() == PaymentStatus.PAID ||
        payment.getStatus() == PaymentStatus.FAIL ||
        payment.getStatus() == PaymentStatus.REFUNDED) {
      return; // 또는 예외
    }

    // TODO 테스트 완료 시 해당 메서드 삭제 예정
    // 결제 성공 테스트용
    if (mode.equals("SUCCESS")) {
      payment.markAsPaid();
      payment.updateImpUid(impUid);

      outboxRecorder.record(
          EventType.PAYMENT_SUCCESS.name(),
          AggregateType.PAYMENT.name(),
          EventType.PAYMENT_SUCCESS.getKafkaTopic(),
          payment.getOrderId(),
          payment.getOrderId().toString(),
          () ->  PaymentSuccessEvent.builder()
              .merchantUid(merchantUid)
              .orderId(payment.getOrderId())
              .build()
      );
    }
    // 결제 실패 테스트용
    else if (mode.equals("FAIL")) {
      payment.markAsFail();
      outboxRecorder.record(
          EventType.PAYMENT_FAIL.name(),
          AggregateType.PAYMENT.name(),
          EventType.PAYMENT_FAIL.getKafkaTopic(),
          payment.getOrderId(),
          payment.getOrderId().toString(),
          () ->  PaymentFailEvent.builder()
              .merchantUid(merchantUid)
              .orderId(payment.getOrderId())
              .build()
      );
    }
    // 결제 취소
    else if (mode.equals("CANCEL")) {
      if (payment.getStatus() == PaymentStatus.PENDING) {
        payment.markAsFail();
        outboxRecorder.record(
            EventType.PAYMENT_FAIL.name(),
            AggregateType.PAYMENT.name(),
            EventType.PAYMENT_FAIL.getKafkaTopic(),
            payment.getOrderId(),
            payment.getOrderId().toString(),
            () ->  PaymentFailEvent.builder()
                .merchantUid(merchantUid)
                .orderId(payment.getOrderId())
                .build()
        );
      }
    }
  }
}
