package com.bappul.delivery.application.event.consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bappul.delivery.application.event.contracts.order.OrderReadyEvent;
import com.bappul.delivery.application.fixture.event.OrderEventFixture;
import com.bappul.delivery.application.service.RiderRedisService;
import com.bappul.delivery.application.validator.DeliveryValidator;
import com.bappul.delivery.domain.entity.Delivery;
import com.bappul.delivery.domain.entity.DeliveryStatus;
import com.bappul.delivery.domain.repository.DeliveryRepository;
import com.bappul.delivery.exception.ServiceExceptionCode;
import com.bappul.event.kafka.KafkaEventProcessor;
import exception.ServiceException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Point;

@SpringBootTest
class DeliveryEventProcessorTest {
  @InjectMocks
  DeliveryEventProcessor target;

  @Mock
  DeliveryRepository deliveryRepository;
  @Mock
  KafkaEventProcessor eventProcessor;
  @Mock
  DeliveryValidator deliveryValidator;
  @Mock
  RiderRedisService riderRedisService;
  
  @Test
  @DisplayName("배달 상태가 '배차중 이상'이면 아무 동작도 수행하지 않는다.")
  void shouldDoNothingWhenStatusIsDispatchingOrAbove() {
    /* given */
    Delivery delivery = mock(Delivery.class);
    OrderReadyEvent event = OrderEventFixture.readyEvent();

    when(deliveryValidator.getByOrderId(1L)).thenReturn(delivery);
    when(delivery.getStatus()).thenReturn(DeliveryStatus.DISPATCHING);

    /* when */
    target.processOrderReadyEventLogic(event);

    /* then */
    verify(delivery, never()).markAsDispatching();
    verify(riderRedisService, never()).findNearbyRiders(any(Point.class), anyDouble(), anyInt());
  }

  @Test
  @DisplayName("첫 시도에서 라이더를 찾으면 추가 탐색 없이 배차 상태로 변경한다.")
  void shouldMarkDispatchingWhenRidersFoundOnFirstTry() {
    /* given */
    Delivery delivery = mock(Delivery.class);
    OrderReadyEvent event = OrderEventFixture.readyEvent();

    when(deliveryValidator.getByOrderId(1L)).thenReturn(delivery);
    when(delivery.getStatus()).thenReturn(DeliveryStatus.REQUESTED);
    when(riderRedisService.findNearbyRiders(any(Point.class), eq(0.5), eq(3)))
        .thenReturn(List.of(1L, 2L));

    /* when */
    target.processOrderReadyEventLogic(event);

    /* then */
    verify(riderRedisService, times(1)).findNearbyRiders(any(Point.class), eq(0.5), eq(3));
    verify(delivery, times(1)).markAsDispatching();
  }


  @Test
  @DisplayName("첫 시도에서 라이더를 찾지 못하면 반경과 리밋을 늘리며 재탐색한다.")
  void shouldRetryWithExpandedRadiusAndLimitWhenNoRiderFoundInitially() {
    /* given */
    Delivery delivery = mock(Delivery.class);
    OrderReadyEvent event = OrderEventFixture.readyEvent();

    when(deliveryValidator.getByOrderId(1L)).thenReturn(delivery);
    when(delivery.getStatus()).thenReturn(DeliveryStatus.REQUESTED);
    when(riderRedisService.findNearbyRiders(any(Point.class), eq(0.5), eq(3))).thenReturn(List.of());
    when(riderRedisService.findNearbyRiders(any(Point.class), eq(1.0), eq(6))).thenReturn(List.of());
    when(riderRedisService.findNearbyRiders(any(Point.class), eq(2.0), eq(12))).thenReturn(List.of(1L));

    /* when */
    target.processOrderReadyEventLogic(event);

    /* then */
    verify(riderRedisService, times(3)).findNearbyRiders(any(Point.class), anyDouble(), anyInt());
    verify(delivery, times(1)).markAsDispatching();
  }


  @Test
  @DisplayName("세 번의 재탐색 후에도 라이더가 없으면 예외를 발생시킨다.")
  void shouldThrowExceptionWhenNoRidersFoundAfterAllRetries() {
    /* given */
    Delivery delivery = mock(Delivery.class);
    OrderReadyEvent event = OrderEventFixture.readyEvent();

    when(deliveryValidator.getByOrderId(1L)).thenReturn(delivery);
    when(delivery.getStatus()).thenReturn(DeliveryStatus.REQUESTED);
    when(riderRedisService.findNearbyRiders(any(Point.class), eq(0.5), eq(3))).thenReturn(List.of());
    when(riderRedisService.findNearbyRiders(any(Point.class), eq(1.0), eq(6))).thenReturn(List.of());
    when(riderRedisService.findNearbyRiders(any(Point.class), eq(2.0), eq(12))).thenReturn(List.of());


    /* when, then */
    ServiceException ex = assertThrows(ServiceException.class, () -> target.processOrderReadyEventLogic(event));
    assertThat(ex.getCode()).isEqualTo(ServiceExceptionCode.NO_NEARBY_RIDER.name());
    verify(riderRedisService, times(3)).findNearbyRiders(any(Point.class), anyDouble(), anyInt());
  }
}
