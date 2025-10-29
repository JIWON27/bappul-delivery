package com.bappul.order.application.validator;

import static com.bappul.order.exception.ServiceExceptionCode.INVALID_ORDER_STATUS;
import static com.bappul.order.exception.ServiceExceptionCode.NOT_FOUND_ORDER;
import static com.bappul.order.exception.ServiceExceptionCode.UNAUTHORIZED_ORDER_ACCESS;

import com.bappul.order.domain.entitiy.Order;
import com.bappul.order.domain.entitiy.OrderStatus;
import com.bappul.order.domain.repository.OrderRepository;
import exception.ServiceException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderValidator {

  private final OrderRepository orderRepository;

  public boolean idempotencyKeyGuard(String idempotencyKey) {
    return orderRepository.existsByIdempotencyKey(idempotencyKey);
  }

  public Order getOrderByIdempotencyKey(String idempotencyKey) {
    return orderRepository.findByIdempotencyKey((idempotencyKey))
        .orElseThrow(() -> new ServiceException(NOT_FOUND_ORDER));
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

  public void ensureOwnedBy(Order order, Long userId) {
    if (!order.getUserId().equals(userId)) {
      throw new ServiceException(UNAUTHORIZED_ORDER_ACCESS);
    }
  }

}
