package com.bappul.order.domain.repository;

import com.bappul.order.domain.entitiy.OrderItemOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemOptionRepository extends JpaRepository<OrderItemOption, Long> {

}
