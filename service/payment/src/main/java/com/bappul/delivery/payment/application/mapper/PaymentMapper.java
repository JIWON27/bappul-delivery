package com.bappul.delivery.payment.application.mapper;

import com.bappul.delivery.payment.adapter.response.PaymentResponse;
import com.bappul.delivery.payment.domain.entity.Payment;
import com.bappul.delivery.payment.domain.entity.PaymentIntent;
import com.bappul.delivery.payment.web.v1.request.PaymentIntentRequest;
import com.bappul.delivery.payment.web.v1.response.PaymentIntentResponse;
import java.time.LocalDateTime;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

  @Mapping(target = "currency", constant = "KRW")
  @Mapping(target = "payMethod", source = "request.payMethod")
  PaymentIntent toPaymentIntent(PaymentIntentRequest request, String paymentId, String channelKey);

  @Mapping(target = "orderId",         source = "intent.orderId")
  @Mapping(target = "merchantUid",     source = "response.merchantId")
  @Mapping(target = "pgTransactionId", source = "response.pgTxId")
  @Mapping(target = "paymentId",       source = "response.id")
  @Mapping(target = "approvedPrice",   source = "response.amount.paid")
  @Mapping(target = "pgProvider",      source = "response.channel.pgProvider")
  @Mapping(target = "payMethod",       source = "intent.payMethod")
  @Mapping(target = "transactionId",   source = "response.transactionId")
  @Mapping(target = "paidAt", source = "paidAt")
  @Mapping(target = "status",          constant = "PAID")
  @Mapping(target = "currency",        constant = "KRW")
  Payment toPayment(PaymentIntent intent, PaymentResponse response, LocalDateTime paidAt);
  PaymentIntentResponse toPaymentIntentResponse(PaymentIntent paymentIntent, String storeId);
}
