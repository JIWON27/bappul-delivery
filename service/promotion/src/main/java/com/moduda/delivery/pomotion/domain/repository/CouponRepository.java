package com.moduda.delivery.pomotion.domain.repository;

import com.moduda.delivery.pomotion.domain.entity.Coupon;
import com.moduda.delivery.pomotion.domain.entity.CouponStatus;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

  boolean existsByCouponPolicyIdAndUserIdAndStatus(Long couponPolicyId, Long userId, CouponStatus status);
  long countByUserIdAndCouponPolicyId(Long userId, Long couponPolicyId);
  List<Coupon> findAllByUserId(Long userId);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT c FROM Coupon c WHERE  c.code = :code")
  Optional<Coupon> findByCodeWithLock(@Param("code") String code);

  @Query("SELECT c.code FROM Coupon c WHERE c.code IN :codes")
  List<String> findExistingCodes(Set<String> codes);
}
