package com.bappul.delivery.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.bappul.delivery.application.fixture.rider.RiderFixture;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@DataRedisTest
class RiderRedisServiceTest {

  static GenericContainer<?> redis =
      new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
          .withExposedPorts(6379);

  @DynamicPropertySource
  static void redisProperties(DynamicPropertyRegistry registry) {
    redis.start();
    registry.add("spring.data.redis.host", redis::getHost);
    registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
  }

  @Autowired
  RedisTemplate<String, String> redisTemplate;

  RiderRedisService riderRedisService;

  @BeforeEach
  void setUp() {
    Point storePoint = RiderFixture.STORE_POINT;
    int nearRiderCount = 5;
    int farRiderCount = 5;

    riderRedisService = new RiderRedisService(redisTemplate);
    RiderFixture.resetRedis(redisTemplate);

    RiderFixture.createNearbyRiders(redisTemplate, storePoint, 0.5, nearRiderCount);
    RiderFixture.createFarByRiders(redisTemplate, storePoint, 0.5, nearRiderCount, farRiderCount);
  }

  @Test
  @DisplayName("배차 라이더 후보가 많아도 선정 개수는 리밋을 초과하지 않는다.")
  public void shouldReturnLimitedRidersWhenMoreThanLimit() {
    /* given */
    Point storePoint = RiderFixture.STORE_POINT;
    double INITIAL_RADIUS = 0.5;
    int RIDER_LIMIT = 3;

    /* when */
    List<Long> riderIds = riderRedisService.findNearbyRiders(storePoint, INITIAL_RADIUS, RIDER_LIMIT);

    /* then */
    assertThat(riderIds).hasSize(3);
    assertThat(riderIds.size()).isLessThanOrEqualTo(RIDER_LIMIT);
  }

  @Test
  @DisplayName("배차 후보 라이더가 없을 경우 빈 결과를 반환한다")
  public void shouldReturnEmptyListWhenNoDispatchCandidates() {
    /* given */
    Point storePoint = new Point(RiderFixture.STORE_POINT.getX() + 10, RiderFixture.STORE_POINT.getY() + 10);
    double INITIAL_RADIUS = 0.5;
    int RIDER_LIMIT = 5;

    /* when */
    List<Long> riderIds = riderRedisService.findNearbyRiders(storePoint, INITIAL_RADIUS, RIDER_LIMIT);

    /* then */
    assertThat(riderIds).hasSize(0);
  }

  @Test
  @DisplayName("반경 내 라이더 수가 제한보다 적으면 있는 만큼만 반환한다.")
  public void shouldReturnAllFoundWhenLessThanLimit() {
    /* given */
    Point storePoint = RiderFixture.STORE_POINT;
    double INITIAL_RADIUS = 0.5;
    int RIDER_LIMIT = 10;

    /* when */
    List<Long> riderIds = riderRedisService.findNearbyRiders(storePoint, INITIAL_RADIUS, RIDER_LIMIT);

    /* then */
    assertThat(riderIds).hasSize(5);
    assertThat(riderIds.size()).isLessThanOrEqualTo(RIDER_LIMIT);
  }

}
