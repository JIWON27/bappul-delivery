package com.bappul.delivery.payment.application.service;

import static com.bappul.delivery.payment.exception.ServiceExceptionCode.AMOUNT_MISMATCH;
import static com.bappul.delivery.payment.exception.ServiceExceptionCode.INVALID_ARGUMENT_ORDER_ID;
import static com.bappul.delivery.payment.exception.ServiceExceptionCode.MERCHANT_UID_MISMATCH;
import static com.bappul.delivery.payment.exception.ServiceExceptionCode.NOT_PAID_STATUS;

import com.bappul.delivery.payment.adapter.response.PaymentResponse;
import com.bappul.delivery.payment.application.event.contracts.common.AggregateType;
import com.bappul.delivery.payment.application.event.contracts.common.EventType;
import com.bappul.delivery.payment.application.event.contracts.payment.PaymentFailEvent;
import com.bappul.delivery.payment.application.event.contracts.payment.PaymentSuccessEvent;
import com.bappul.delivery.payment.application.mapper.PaymentMapper;
import com.bappul.delivery.payment.application.validator.PaymentValidator;
import com.bappul.delivery.payment.domain.entity.PaymentIntent;
import com.bappul.delivery.payment.domain.repository.PaymentIntentRepository;
import com.bappul.delivery.payment.domain.repository.PaymentRepository;
import com.bappul.delivery.payment.port.PortOnePort;
import com.bappul.delivery.payment.web.v1.request.PaymentIntentRequest;
import com.bappul.delivery.payment.web.v1.request.PaymentVerifyRequest;
import com.bappul.delivery.payment.web.v1.request.PaymentWebhookRequest;
import com.bappul.delivery.payment.web.v1.response.PaymentIntentResponse;
import com.bappul.event.outbox.OutboxRecorder;
import exception.ServiceException;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

  private final PortOnePort portOnePort;

  private final OutboxRecorder outboxRecorder;
  private final PaymentRepository paymentRepository;
  private final PaymentIntentRepository paymentIntentRepository;

  private final PaymentMapper paymentMapper;
  private final PaymentValidator paymentValidator;
  private final ChannelKeyProperties channelKeyProperties;
  private final Clock clock;

  @Value("${portone.storeId}")
  private String storeId;

  @Transactional
  public void createIntent(PaymentIntentRequest request) {
    String paymentId = paymentIdGenerate(request.getOrderId());
    String channelKey = channelKeyProperties.getChannels().get(request.getPgProvider());

    PaymentIntent paymentIntent = paymentIntentRepository.save(paymentMapper.toPaymentIntent(request, paymentId, channelKey));
    PaymentIntent savedPaymentIntent = paymentIntentRepository.save(paymentIntent);
    savedPaymentIntent.updatePaymentId(paymentId);

    preparePayment(paymentIntent.getExpectedPrice(), paymentId);
  }

  @Transactional(readOnly = true)
  public PaymentIntentResponse getPaymentIntent(Long orderId) {
    PaymentIntent paymentIntent = paymentValidator.getPaymentIntentByOrderId(orderId);
    return paymentMapper.toPaymentIntentResponse(paymentIntent, storeId);
  }

  @Transactional
  public void paymentVerify(PaymentVerifyRequest request) {
    String paymentId = request.getPaymentId();

    PaymentIntent paymentIntent = paymentValidator.getPaymentIntentByPaymentId(paymentId);

    try {
      PaymentResponse paymentResponse = portOnePort.getPayment(paymentId);

      if (paymentRepository.existsByPaymentId(paymentResponse.getId())) {
        return;
      }

      validatePayment(paymentResponse, paymentIntent);

      com.bappul.delivery.payment.domain.entity.Payment payment = paymentMapper.toPayment(paymentIntent, paymentResponse, LocalDateTime.ofInstant(paymentResponse.getPaidAt(), clock.getZone()));
      try {
        paymentRepository.save(payment);
      } catch (DataIntegrityViolationException dup) {
        log.info("duplicate paymentId={}, skip insert", paymentId);
        return;
      }

      outboxRecorder.record(
          EventType.PAYMENT_SUCCESS.name(),
          AggregateType.PAYMENT.name(),
          EventType.PAYMENT_SUCCESS.getKafkaTopic(),
          paymentIntent.getOrderId(),
          paymentIntent.getOrderId().toString(),
          () -> new PaymentSuccessEvent(paymentIntent.getOrderId(), paymentIntent.getPaymentId())
      );
    } catch (ServiceException e) {
      outboxRecorder.recordFail(
          EventType.PAYMENT_FAIL.name(),
          AggregateType.PAYMENT.name(),
          EventType.PAYMENT_FAIL.getKafkaTopic(),
          paymentIntent.getOrderId(),
          paymentIntent.getOrderId().toString(),
          () -> new PaymentFailEvent(paymentIntent.getOrderId(), paymentIntent.getPaymentId())
      );
      throw e;
    }
  }

  @Transactional
  public void processWebhook(PaymentWebhookRequest request) {
    String type = request.getType(); // 이벤트 타입
    String paymentId = request.getData().getPaymentId(); // 결제 ID

    if (paymentRepository.existsByPaymentId(paymentId)) {
      return;
    }

    PaymentIntent paymentIntent = paymentValidator.getPaymentIntentByPaymentId(paymentId);
    PaymentResponse paymentResponse = portOnePort.getPayment(paymentId);
    validatePayment(paymentResponse, paymentIntent);
    com.bappul.delivery.payment.domain.entity.Payment payment = paymentMapper.toPayment(paymentIntent, paymentResponse, LocalDateTime.ofInstant(paymentResponse.getPaidAt(), clock.getZone()));
    try {
      paymentRepository.save(payment);
    } catch (DataIntegrityViolationException dup) {
      log.info("duplicate paymentId={}, skip insert", paymentId);
      return;
    }

    switch (type) {
      case "Transaction.Paid":
        outboxRecorder.record(
            EventType.PAYMENT_SUCCESS.name(),
            AggregateType.PAYMENT.name(),
            EventType.PAYMENT_SUCCESS.getKafkaTopic(),
            paymentIntent.getOrderId(),
            paymentIntent.getOrderId().toString(),
            () -> new PaymentSuccessEvent(paymentIntent.getOrderId(), paymentIntent.getPaymentId())
        );
        break;
      case "Transaction.Cancelled":
        outboxRecorder.recordFail(
            EventType.PAYMENT_FAIL.name(),
            AggregateType.PAYMENT.name(),
            EventType.PAYMENT_FAIL.getKafkaTopic(),
            paymentIntent.getOrderId(),
            paymentIntent.getOrderId().toString(),
            () -> new PaymentFailEvent(paymentIntent.getOrderId(), paymentIntent.getPaymentId())
        );
        break;
      default:
        log.warn("알 수 없는 이벤트 타입: {}", type);
    }
  }

  private void preparePayment(BigDecimal expectedPrice, String paymentId){
    portOnePort.preRegister(expectedPrice, paymentId);
  }

  private void validatePayment(PaymentResponse paymentResponse, PaymentIntent paymentIntent) {
    PaymentResponse response = Objects.requireNonNull(paymentResponse);

    if(!"PAID".equalsIgnoreCase(response.getStatus().name())) {
      throw new ServiceException(NOT_PAID_STATUS);
    }

    if(!Objects.equals(paymentIntent.getPaymentId(), paymentIntent.getPaymentId())) {
      throw new ServiceException(MERCHANT_UID_MISMATCH);
    }

    if(paymentIntent.getExpectedPrice().compareTo(response.getAmount().getPaid()) != 0) {
      throw new ServiceException(AMOUNT_MISMATCH);
    }
  }

  private String paymentIdGenerate(Long orderId) {
    if (Objects.isNull(orderId) || orderId <= 0) {
      throw new ServiceException(INVALID_ARGUMENT_ORDER_ID);
    }
    String uid = UUID.randomUUID().toString().replace("-", "").substring(0, 20);
    return "order_" + orderId + "_" + uid;
  }
}
