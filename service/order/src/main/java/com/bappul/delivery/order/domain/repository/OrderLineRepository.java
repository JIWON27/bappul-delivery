package com.bappul.delivery.order.domain.repository;

import com.bappul.delivery.order.domain.entitiy.OrderLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderLineRepository extends JpaRepository<OrderLine, Long> {

}
