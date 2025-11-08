package com.bappul.delivery.application.service;

import com.bappul.delivery.domain.entity.RiderStatus;
import com.bappul.delivery.web.v1.request.RiderLocation;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoLocation;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoRadiusCommandArgs;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RiderRedisService {
  private final RedisTemplate<String, String> redisTemplate;

  private static final String GEO_KEY = "riders:geo";
  private static final String LAST_UPDATE_KEY = "riders:%s:last";

  public void addRiderGeo(RiderLocation riderLocation, Long riderUserId) {
    double latitude = riderLocation.getLatitude();
    double longitude = riderLocation.getLongitude();
    RiderStatus status = riderLocation.getStatus();
    String riderId = String.valueOf(riderUserId);

    if (status == RiderStatus.OFFLINE) {
      redisTemplate.opsForGeo().remove(GEO_KEY, riderId);
      return;
    }

    redisTemplate.opsForGeo().add(GEO_KEY, new Point(longitude, latitude), riderId);
  }

  public void updateRiderLastUpdateTime(Long riderId){
    String lastUpdateKey = LAST_UPDATE_KEY.formatted(riderId);
    redisTemplate.opsForValue().set(lastUpdateKey, Instant.now().toString(), Duration.ofSeconds(30));
  }

  public List<Long> findNearbyRiders(Point point, double radius, int riderLimit) {
    Circle circle = new Circle(point, new Distance(radius, Metrics.KILOMETERS));

    GeoRadiusCommandArgs commandArgs = GeoRadiusCommandArgs.newGeoRadiusArgs()
        .includeDistance()
        .sortAscending()
        .limit(riderLimit * 3L);

    GeoResults<GeoLocation<String>> results = redisTemplate.opsForGeo().radius(GEO_KEY, circle, commandArgs);

    if (Objects.isNull(results) || results.getContent().isEmpty()) {
      return Collections.emptyList();
    }

    List<Long> riderIds = results.getContent().stream()
        .map(r -> Long.valueOf(r.getContent().getName()))
        .filter(this::isLatestUpdatedRider)
        .toList();

    if (riderIds.isEmpty()) {
      return Collections.emptyList();
    }
    return riderIds.subList(0, Math.min(riderLimit, riderIds.size()));
  }

  private boolean isLatestUpdatedRider(Long riderId) {
    String lastUpdateKey = LAST_UPDATE_KEY.formatted(riderId);
    return redisTemplate.hasKey(lastUpdateKey);
  }


}
