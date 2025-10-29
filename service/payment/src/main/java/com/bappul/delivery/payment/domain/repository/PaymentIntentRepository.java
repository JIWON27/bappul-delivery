package com.bappul.delivery.payment.domain.repository;

import com.bappul.delivery.payment.domain.entity.PaymentIntent;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentIntentRepository extends JpaRepository<PaymentIntent, Long> {
  Optional<PaymentIntent> findByOrderId(Long orderId);
  Optional<PaymentIntent> findByPaymentId(String paymentId);
}
