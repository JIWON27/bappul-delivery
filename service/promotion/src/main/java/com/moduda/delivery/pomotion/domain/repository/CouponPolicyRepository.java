package com.moduda.delivery.pomotion.domain.repository;

import com.moduda.delivery.pomotion.domain.entity.CouponPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponPolicyRepository extends JpaRepository<CouponPolicy,Long> {

}
