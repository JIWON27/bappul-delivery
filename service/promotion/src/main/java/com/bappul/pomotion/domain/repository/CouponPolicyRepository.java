package com.bappul.pomotion.domain.repository;

import com.bappul.pomotion.domain.entity.CouponPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponPolicyRepository extends JpaRepository<CouponPolicy,Long> {

}
