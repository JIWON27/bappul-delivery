package com.bappul.delivery.user.domain.repository;

import com.bappul.delivery.user.domain.entity.address.Address;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address,Long> {
  Optional<Address> findByUserId(Long userId);
}
