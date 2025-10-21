package com.bappul.delivery.application.event.consumer;

import static com.bappul.delivery.exception.ServiceExceptionCode.JSON_DESERIALIZATION_ERROR;

import com.bappul.delivery.application.event.contracts.order.OrderAcceptEvent;
import com.bappul.delivery.application.event.contracts.order.OrderReadyEvent;
import com.bappul.delivery.application.validator.DeliveryValidator;
import com.bappul.delivery.domain.entity.Delivery;
import com.bappul.delivery.domain.entity.DeliveryStatus;
import com.bappul.delivery.domain.repository.DeliveryRepository;
import com.bappul.event.inbox.InboxEvent;
import com.bappul.event.inbox.InboxEventRepository;
import com.bappul.event.inbox.InboxStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import exception.ServiceException;
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
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KafkaEventProcessor {

  private final DeliveryRepository deliveryRepository;
  private final InboxEventRepository inboxEventRepository;

  private final DeliveryValidator deliveryValidator;
  private final ObjectMapper objectMapper;

  private final RedisTemplate<String ,String> redisTemplate;
  private final String ADMIN_CODE_RIDER_LOCATION = "riders:admin:geo:avail:%s";


  @Transactional
  public void processOrderAcceptEvent(String eventId, String eventType, String payload) {
    InboxEvent inboxEvent = validateAndSaveInboxEvent(eventId,  eventType, payload);

    try {
      OrderAcceptEvent event = objectMapper.readValue(payload, OrderAcceptEvent.class);

      Delivery delivery = Delivery.builder()
          .orderId(event.getOrderId())
          .riderUserId(null)
          .status(DeliveryStatus.REQUESTED)
          .build();
      deliveryRepository.save(delivery);

      inboxEvent.markAsProcessed();

    } catch (JsonProcessingException e) {
      throw new ServiceException(JSON_DESERIALIZATION_ERROR);
    }

  }

  @Transactional
  public void processOrderReadyEvent(String eventId, String eventType, String payload) {
    InboxEvent inboxEvent = validateAndSaveInboxEvent(eventId,  eventType, payload);

    try {
      OrderReadyEvent event = objectMapper.readValue(payload, OrderReadyEvent.class);
      Delivery delivery = deliveryValidator.getByOrderId(event.getOrderId());

      // TODO 배차 알고리즘
      double storeLatitude = event.getLatitude();
      double storeLongitude = event.getLongitude();
      String adminCode = event.getAdminCode();

      double[] radiiKm = {0.5, 1.0, 2.0}; // 점점 라이더 범위 넓히기 위한 배열

      /**
       * 가게로부터 범위 근거리 내 라이더 추출
       */
      List<String> nearestRiderIds = null;
      for (double km : radiiKm) {
        Circle circle = new Circle(
            new Point(storeLongitude, storeLatitude),
            new Distance(km, Metrics.KILOMETERS)
        );

        GeoRadiusCommandArgs geoRedisCommand = GeoRadiusCommandArgs.newGeoRadiusArgs()
            .includeDistance()
            .sortAscending()
            .limit(3);

        String riderLocationKey = ADMIN_CODE_RIDER_LOCATION.formatted(adminCode);
        GeoResults<GeoLocation<String>> geoResults = redisTemplate.opsForGeo()
            .radius(riderLocationKey, circle, geoRedisCommand);

        if (Objects.isNull(geoResults) || geoResults.getContent().isEmpty()) {
          // 다음 거리로 넘어감
          continue;
        }

        nearestRiderIds = geoResults.getContent().stream()
            .map(geoLocationGeoResult -> geoLocationGeoResult.getContent().getName()).toList();
        if (!nearestRiderIds.isEmpty()) {
          break;
        }
      }

      if (Objects.isNull(nearestRiderIds) || nearestRiderIds.isEmpty()) {
        // TODO 라이더 후보가 한명도 안나왔을 때 인접 행정동으로 이동 등 추가 처리 고민
        return;
      }

      // TODO 추출한 nearestRiderIds 라이더들에게 배달 알림
      inboxEvent.markAsProcessed();

    } catch (JsonProcessingException e) {
      throw new ServiceException(JSON_DESERIALIZATION_ERROR);
    }
  }

  private InboxEvent validateAndSaveInboxEvent(String eventId, String eventType, String payload) {
    boolean exists = inboxEventRepository.existsByEventId(eventId);
    if (exists) {
      return inboxEventRepository.findByEventId(eventId);
    }

    InboxEvent inboxEvent = InboxEvent.builder()
        .eventId(eventId)
        .eventType(eventType)
        .payload(payload)
        .status(InboxStatus.RECEIVED)
        .build();
    return inboxEventRepository.save(inboxEvent);
  }
}
