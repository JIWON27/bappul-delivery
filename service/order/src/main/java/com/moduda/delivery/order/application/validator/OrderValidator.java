package com.moduda.delivery.order.application.validator;

import static com.moduda.delivery.order.exception.ServiceExceptionCode.IDEMPOTENCYKEY;
import static com.moduda.delivery.order.exception.ServiceExceptionCode.NOT_FOUND_ORDER;

import com.moduda.delivery.order.domain.entitiy.Order;
import com.moduda.delivery.order.domain.repository.OrderRepository;
import exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderValidator {

  private final OrderRepository orderRepository;

  public void validateIdempotencyKey(String idempotencyKey) {
    boolean exist = orderRepository.existsByIdempotencyKey(idempotencyKey);
    if (exist) {
      // TODO 예외 코드 고민
      throw new ServiceException(IDEMPOTENCYKEY);
    }
  }

  public Order getOrderById(Long orderId) {
    return orderRepository.findById(orderId)
        .orElseThrow(() -> new ServiceException(NOT_FOUND_ORDER));
  }

}
