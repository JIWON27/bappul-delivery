package com.bappul.delivery.order.application.validator;

import static com.bappul.delivery.order.exception.ServiceExceptionCode.IDEMPOTENCYKEY;
import static com.bappul.delivery.order.exception.ServiceExceptionCode.INVALID_ORDER_STATUS;
import static com.bappul.delivery.order.exception.ServiceExceptionCode.NOT_FOUND_ORDER;
import static com.bappul.delivery.order.exception.ServiceExceptionCode.UNAUTHORIZED_ORDER_ACCESS;

import com.bappul.delivery.order.domain.entitiy.Order;
import com.bappul.delivery.order.domain.entitiy.OrderStatus;
import com.bappul.delivery.order.domain.repository.OrderRepository;
import exception.ServiceException;
import java.util.Objects;
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

  public void validateBelongsToStore(Long orderStoreId, Long pathStoreId) {
    if (!Objects.equals(orderStoreId, pathStoreId)) {
      throw new ServiceException(UNAUTHORIZED_ORDER_ACCESS);
    }
  }

  public void validateAcceptable(Order order) {
    if (order.getOrderStatus() != OrderStatus.PAID) {
      throw new ServiceException(INVALID_ORDER_STATUS);
    }
  }

  public void validateRejectable(Order o) {
    if (o.getOrderStatus() != OrderStatus.PAID)
      throw new ServiceException(INVALID_ORDER_STATUS);
  }
  public void validateReadyable(Order o) {
    if (o.getOrderStatus() != OrderStatus.ACCEPTED)
      throw new ServiceException(INVALID_ORDER_STATUS);
  }

}
