package com.moduda.delivery.payment.application.service;

import static com.moduda.delivery.payment.exception.ServiceExceptionCode.JSON_SERIALIZATION_ERROR;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moduda.delivery.payment.application.event.contracts.common.AggregateType;
import com.moduda.delivery.payment.application.event.contracts.common.EventType;
import com.moduda.delivery.payment.application.event.contracts.payment.PaymentFailEvent;
import com.moduda.delivery.payment.application.event.contracts.payment.PaymentSuccessEvent;
import com.moduda.delivery.payment.application.event.producer.OutboxRecorded;
import com.moduda.delivery.payment.application.validator.PaymentValidator;
import com.moduda.delivery.payment.domain.entity.OutBoxEvent;
import com.moduda.delivery.payment.domain.entity.OutboxStatus;
import com.moduda.delivery.payment.domain.entity.Payment;
import com.moduda.delivery.payment.domain.entity.PaymentStatus;
import com.moduda.delivery.payment.domain.repository.OutboxRepository;
import com.moduda.delivery.payment.domain.repository.PaymentRepository;
import com.moduda.delivery.payment.web.v1.request.PaymentCreateRequest;
import com.moduda.delivery.payment.web.v1.request.PaymentValidationRequest;
import com.siot.IamportRestClient.IamportClient;
import exception.ServiceException;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

  private final RefundService refundService;
  private final IamportClient iamportClient;

  private final OutboxRepository outboxRepository;
  private final PaymentRepository paymentRepository;

  private final PaymentValidator paymentValidator;
  private final MerchantUidGenerator merchantUidGenerator;
  private final ObjectMapper objectMapper;
  private final ApplicationEventPublisher eventPublisher;

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

      OutboxRecorded outboxRecorded = recordPaymentSuccessEvent(merchantUid, payment.getOrderId(), payment.getId());
      eventPublisher.publishEvent(outboxRecorded);
    }
    // 결제 실패 테스트용
    else if (mode.equals("FAIL")) {
      payment.markAsFail();
      OutboxRecorded outboxRecorded = recordPaymentFailEvent(merchantUid, payment.getOrderId(), payment.getId());
      eventPublisher.publishEvent(outboxRecorded);
    }
    // 결제 취소
    else if (mode.equals("CANCEL")){
      if (payment.getStatus() == PaymentStatus.PENDING) {
        payment.markAsFail();
        OutboxRecorded e = recordPaymentFailEvent(merchantUid, payment.getOrderId(), payment.getId());
        eventPublisher.publishEvent(e);
      }
    }
  }

  // TODO PG 관련 로직 잠시 주석 처리
  /*@Transactional
  public String preparePayment(PaymentCreateRequest request, Long userId){
    try {
      String merchantUid = createMerchantUid(request.getOrderId());

      PrepareData pgReq = new PrepareData(merchantUid,  request.getPaypalPrice());
      IamportResponse<Prepare> prepareIamportResponse = iamportClient.postPrepare(pgReq);
      if (prepareIamportResponse.getCode() != 0) {
        throw new ServiceException("결제정보 사전등록 실패");
      }

      com.moduda.delivery.payment.domain.entity.Payment payment = com.moduda.delivery.payment.domain.entity.Payment.builder()
          .orderId(request.getOrderId())
          .userId(userId)
          .status(PaymentStatus.PENDING)
          .impUid(null)
          .merchantUid(merchantUid)
          .price(request.getPaypalPrice())
          .build();

      paymentRepository.save(payment);
      return merchantUid;
    } catch (IOException | IamportResponseException e) {
      throw new RuntimeException(e);
    }
  }

  @Transactional
  public void validationPayment(PaymentValidationRequest request) {
    try {
      String impUid = request.getImpUid();
      String merchantUid = request.getMerchantUid();

      IamportResponse<Payment> response = iamportClient.paymentByImpUid(request.getImpUid());

      String pgMerchantUid = response.getResponse().getMerchantUid();
      String pgImpUid = response.getResponse().getImpUid();
      BigDecimal price = response.getResponse().getAmount();

      com.moduda.delivery.payment.domain.entity.Payment payment = paymentValidator.getPaymentByMerchantUid(merchantUid);

      if(!merchantUid.equals(pgMerchantUid)) {
        throw PaymentValidationException.builder()
            .impUid(impUid)
            .merchantUid(merchantUid)
            .price(price)
            .orderId(payment.getOrderId())
            .paymentId(payment.getId())
            .message("PG 결제 검증 실패")
            .reason(Reason.MERCHANT_UID_MISMATCH)
            .build();
      }


      if (!response.getResponse().getStatus().equalsIgnoreCase("paid")) {
        throw PaymentValidationException.builder()
            .impUid(impUid)
            .merchantUid(merchantUid)
            .price(price)
            .orderId(payment.getOrderId())
            .paymentId(payment.getId())
            .message("PG 결제 검증 실패")
            .reason(Reason.NOT_PAID)
            .build();
      }

      if (!payment.getStatus().equals(PaymentStatus.PENDING) && impUid.equals(pgImpUid)) {
        throw PaymentValidationException.builder()
            .impUid(impUid)
            .merchantUid(merchantUid)
            .price(price)
            .orderId(payment.getOrderId())
            .paymentId(payment.getId())
            .message("PG 결제 검증 실패")
            .reason(Reason.IMP_UID_MISMATCH)
            .build();
      }

      if (payment.getPrice().compareTo(price) != 0) {
        throw PaymentValidationException.builder()
            .impUid(impUid)
            .merchantUid(merchantUid)
            .price(price)
            .orderId(payment.getOrderId())
            .paymentId(payment.getId())
            .message("PG 결제 검증 실패")
            .reason(Reason.AMOUNT_MISMATCH)
            .build();
      }

      payment.markAsPaid();
      payment.updateImpUid(payment.getImpUid());

      OutboxRecorded outboxEventEnqueued = recordPaymentSuccessEvent(impUid, merchantUid, payment.getOrderId(), payment.getId());
      eventPublisher.publishEvent(outboxEventEnqueued);

    } catch (IamportResponseException | IOException e) {
      throw new RuntimeException(e);
    } catch (PaymentValidationException e) {
      switch (e.getReason()) {
        case MERCHANT_UID_MISMATCH, AMOUNT_MISMATCH, IMP_UID_MISMATCH -> {
          refundService.cancelPayment(e.getImpUid());
        }
        case NOT_PAID -> {
          OutboxRecorded outboxEventEnqueued = recordPaymentFailEvent(e.getImpUid(), e.getMerchantUid(), e.getOrderId(), e.getPaymentId());
          eventPublisher.publishEvent(outboxEventEnqueued);
        }
      }
      throw e;
    }
  }*/

  private OutboxRecorded recordPaymentFailEvent(String merchantUid, Long orderId, Long paymentId) {
    try {
      PaymentFailEvent event = PaymentFailEvent.builder()
          .merchantUid(merchantUid)
          .orderId(orderId)
          .build();

      String payload = objectMapper.writeValueAsString(event);
      UUID uuid = UUID.randomUUID();
      outboxRepository.save(OutBoxEvent.builder()
          .eventId(uuid)
          .eventType(EventType.PAYMENT_FAIL)
          .aggregateId(paymentId)
          .aggregateType(AggregateType.PAYMENT)
          .partitionKey(merchantUid)
          .payload(payload)
          .status(OutboxStatus.PENDING)
          .occurredAt(LocalDateTime.now())
          .build());
      return new OutboxRecorded(uuid, EventType.PAYMENT_FAIL);

    } catch (JsonProcessingException e) {
      throw new ServiceException(JSON_SERIALIZATION_ERROR);
    }
  }

  private OutboxRecorded recordPaymentSuccessEvent(String merchantUid, Long orderId, Long paymentId) {
    try {
      PaymentSuccessEvent event = PaymentSuccessEvent.builder()
          .merchantUid(merchantUid)
          .orderId(orderId)
          .build();

      String payload = objectMapper.writeValueAsString(event);

      UUID uuid = UUID.randomUUID();
      outboxRepository.save(OutBoxEvent.builder()
          .eventId(uuid)
          .eventType(EventType.PAYMENT_SUCCESS)
          .aggregateId(paymentId)
          .aggregateType(AggregateType.PAYMENT)
          .partitionKey(merchantUid)
          .payload(payload)
          .status(OutboxStatus.PENDING)
          .occurredAt(LocalDateTime.now())
          .build());
      return new OutboxRecorded(uuid, EventType.PAYMENT_SUCCESS);
    } catch (JsonProcessingException e) {
      throw new ServiceException(JSON_SERIALIZATION_ERROR);
    }
  }
}
