package com.bappul.delivery.payment.domain.repository;

import com.bappul.delivery.payment.domain.entity.Payment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
  Optional<Payment> findByMerchantUid(String merchantUid);
  Optional<Payment> findByOrderId(Long orderId);
}
