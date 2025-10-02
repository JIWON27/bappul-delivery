package com.bappul.delivery.order.domain.repository;

import com.bappul.delivery.order.domain.entitiy.OrderLineOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderLineOptionRepository extends JpaRepository<OrderLineOption, Long> {

}
