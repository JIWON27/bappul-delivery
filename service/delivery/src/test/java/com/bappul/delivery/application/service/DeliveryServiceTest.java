package com.bappul.delivery.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.bappul.delivery.application.fixture.delivery.DeliveryFixture;
import com.bappul.delivery.application.validator.DeliveryValidator;
import com.bappul.delivery.domain.entity.Delivery;
import com.bappul.delivery.domain.entity.DeliveryStatus;
import com.bappul.delivery.domain.repository.DeliveryRepository;
import exception.ServiceException;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class DeliveryServiceTest {

  @Autowired
  DeliveryService deliveryService;

  @Autowired
  DeliveryValidator deliveryValidator;

  @Autowired
  DeliveryRepository deliveryRepository;

  @BeforeEach
  void setUp() {
    deliveryRepository.deleteAll();
    Delivery delivery = DeliveryFixture.createReadyDelivery();
    deliveryRepository.save(delivery);
  }

  @Test
  @DisplayName("동일 배달건에 대해 동시에 수락 요청이 들어와도 단 1명만 배차에 성공한다.")
  void shouldAssignOnlyOneRiderWhenMultipleAcceptRequests() throws InterruptedException {
    /* given, when*/
    Long deliveryId = 1L;
    ConcurrentLinkedQueue<Long> deliveryAcceptFailRiders = new ConcurrentLinkedQueue<>();

    List<Long> riderIds = List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);
    int threadCount = riderIds.size();

    ExecutorService pool = Executors.newFixedThreadPool(threadCount);

    CountDownLatch ready = new CountDownLatch(threadCount);
    CountDownLatch start = new CountDownLatch(1);
    CountDownLatch done = new CountDownLatch(threadCount);

    AtomicInteger success = new AtomicInteger(0);

    for (Long riderId : riderIds) {
      pool.submit(() -> {
        try {
          try {
            ready.countDown();
            start.await();

            try {
              deliveryService.deliveryAccept(deliveryId, riderId);
              success.incrementAndGet();
            } catch (ServiceException e) {
              deliveryAcceptFailRiders.add(riderId);
            }
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
        } finally {
          done.countDown();
        }
      });
    }

    ready.await();
    start.countDown();

    if (!done.await(10, TimeUnit.SECONDS)) {
      throw new RuntimeException("작업 스레드 중 10초안에 동작을 수행하지 못한 스레드가 존재합니다.");
    }

    pool.shutdown();

    /* then */
    Delivery delivery = deliveryValidator.getByDeliveryId(deliveryId);
    assertEquals(threadCount-1, deliveryAcceptFailRiders.size());
    assertEquals(1, success.get());
    assertNotNull(delivery.getRiderUserId());
    assertEquals(DeliveryStatus.ASSIGNED, delivery.getStatus());
  }
}
