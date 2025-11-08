package com.bappul.delivery.application.fixture.rider;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisTemplate;

public class RiderFixture {

  public static final String RIDER_GPS_KEY = "riders:geo";
  public static final String RIDER_LATEST_UPDATE_KEY = "riders:%s:last";
  public static final Duration TTL = Duration.ofSeconds(30);
  private static final double KM_TO_DEG = 0.009; // 위도 1km ≈ 0.009도
  public static final Point STORE_POINT = new Point(127.0310, 37.5000);

  public static void resetRedis(RedisTemplate<String, String> redisTemplate) {
    Set<String> keys = redisTemplate.keys("riders:*");
    for (String key : keys) {
      redisTemplate.delete(key);
    }
  }
  /**
   * 가게 위치와 반경, 생성할 라이더 수를 넘기면 해당 가게로부터 근거리 라이더를 생성해주는 헬퍼 메서드
   * @param storePoint
   * @param radius
   * @param riderCount
   */
  public static void createNearbyRiders(RedisTemplate<String, String> redisTemplate, Point storePoint, double radius, int riderCount) {
    for (int i = 1; i <= riderCount; i++) {
      String riderId = String.valueOf(i);
      Point riderPoint = randomPointWithinRadius(storePoint, radius);
      redisTemplate.opsForGeo().add(RIDER_GPS_KEY, riderPoint, riderId);
      redisTemplate.opsForValue().set(RIDER_LATEST_UPDATE_KEY.formatted(riderId), Instant.now().toString(), TTL);
    }
  }

  /**
   * 가게 위치와 반경, 생성할 라이더 수를 넘기면 해당 가게로부터 장거리 라이더를 생성해주는 헬퍼 메서드
   * @param storePoint
   * @param radius
   * @param riderCount
   */
  public static void createFarByRiders(RedisTemplate<String, String> redisTemplate, Point storePoint, double radius, int start, int riderCount) {
    int end = start + riderCount;
    for (int i = 1+start; i <= end; i++) {
      String riderId = String.valueOf(i);
      Point riderPoint = randomPointOutsideRadius(storePoint, radius);
      redisTemplate.opsForGeo().add(RIDER_GPS_KEY, riderPoint, riderId);
      redisTemplate.opsForValue().set(RIDER_LATEST_UPDATE_KEY.formatted(riderId), Instant.now().toString(), TTL);
    }
  }

  public static Point randomPointWithinRadius(Point center, double radiusKm) {
    ThreadLocalRandom rand = ThreadLocalRandom.current();

    // 거리와 각도를 랜덤하게 생성
    double distanceKm = radiusKm * Math.sqrt(rand.nextDouble()); // 원 내부 고르게 분포
    double angle = rand.nextDouble(0, 2 * Math.PI);

    // 거리(km)를 위도/경도 차이(도)로 변환
    double deltaLat = (distanceKm * KM_TO_DEG) * Math.cos(angle);
    double deltaLon = (distanceKm * KM_TO_DEG * Math.cos(Math.toRadians(center.getY()))) * Math.sin(angle);

    double newLon = center.getX() + deltaLon;
    double newLat = center.getY() + deltaLat;
    return new Point(newLon, newLat);
  }

  public static Point randomPointOutsideRadius(Point center, double radiusKm) {
    ThreadLocalRandom rand = ThreadLocalRandom.current();

    // 반경 밖으로 밀어내기: 반경 + epsilon(기본 50m) + spread(추가거리)
    double epsilonKm = 0.05;
    double spreadKm  = 2.0;
    double distanceKm = radiusKm + epsilonKm + rand.nextDouble(spreadKm);

    // 각도는 0~2π 임의
    double angle = rand.nextDouble(0, 2 * Math.PI);

    // km -> degree (위도는 고정 0.009deg/km, 경도는 위도 보정)
    double kmToDeg = 0.009;
    double latRad  = Math.toRadians(center.getY());
    double deltaLat = (distanceKm * kmToDeg) * Math.cos(angle);
    double deltaLon = (distanceKm * kmToDeg * Math.cos(latRad)) * Math.sin(angle);

    double newLon = center.getX() + deltaLon;
    double newLat = center.getY() + deltaLat;
    return new Point(newLon, newLat);
  }
}
