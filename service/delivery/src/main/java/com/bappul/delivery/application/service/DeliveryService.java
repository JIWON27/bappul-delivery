package com.bappul.delivery.application.service;

import com.bappul.delivery.application.event.contracts.common.AggregateType;
import com.bappul.delivery.application.event.contracts.common.EventType;
import com.bappul.delivery.application.event.contracts.delivery.DeliveryPickUpEvent;
import com.bappul.delivery.application.validator.DeliveryValidator;
import com.bappul.delivery.domain.entity.Delivery;
import com.bappul.delivery.domain.entity.RiderStatus;
import com.bappul.delivery.domain.repository.geo.AdminDongRepository;
import com.bappul.delivery.web.v1.request.RiderLocation;
import com.bappul.event.outbox.OutboxRecorder;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeliveryService {

  private final DeliveryValidator deliveryValidator;
  private final AdminDongRepository adminDongRepository;
  private final OutboxRecorder outboxRecorder;

  private final RedisTemplate<String, String> redisTemplate;

  private final String RIDER_BEFORE_ADMIN_CODE = "rider:admin:%s:before";
  private final String ADMIN_CODE_RIDER_LOCATION = "riders:admin:geo:avail:%s";


  @Transactional
  public void deliveryAccept(Long deliveryId, Long riderUserId){
    /**
     * 배차 알고리즘 실행 후 라이더들에게 배달 배차 알람이 간 후,
     * 라이더들 중 배달 배차를 수락하면 실행될 메서드
     * TODO 배달 접수 시 동시성 고민
     */

    Delivery delivery = deliveryValidator.getByDeliveryId(deliveryId);
    delivery.updateRiderUserId(riderUserId);

    delivery.markAsAssigned();
  }

  @Transactional
  public void deliveryPickUp(Long deliveryId, Long riderUserId){
    Delivery delivery = deliveryValidator.getByDeliveryId(deliveryId);

    outboxRecorder.record(
        EventType.DELIVERY_PICKUP.name(),
        AggregateType.DELIVERY.name(),
        EventType.DELIVERY_PICKUP.getKafkaTopic(),
        delivery.getOrderId(),
        delivery.getOrderId().toString(),
        () -> new DeliveryPickUpEvent(delivery.getOrderId())
    );

    delivery.markAsPickUp();
  }

  @Transactional
  public void deliveryComplete(Long deliveryId){
    Delivery delivery = deliveryValidator.getByDeliveryId(deliveryId);

    outboxRecorder.record(
        EventType.DELIVERY_COMPLETE.name(),
        AggregateType.DELIVERY.name(),
        EventType.DELIVERY_COMPLETE.getKafkaTopic(),
        delivery.getOrderId(),
        delivery.getOrderId().toString(),
        () -> new DeliveryPickUpEvent(delivery.getOrderId())
    );

    delivery.markAsDeliverd();
  }

  @Transactional
  public void updateRiderStatus(RiderLocation riderLocation, Long riderUserId){
    // TODO 가독성
    double latitude = riderLocation.getLatitude();
    double longitude = riderLocation.getLongitude();

    String currentAdminCode = adminDongRepository.findAdminCodeByGPS(longitude, latitude);
    String riderBeforeAdminCodeKey = RIDER_BEFORE_ADMIN_CODE.formatted(String.valueOf(riderUserId));

    /** OFFLINE
     * 라이더 오프라인으로 상태로 변경하면,
     * 라이더의 관련 배달 가능 상태 set에서 모두 삭제(이전 행정동, 현재 행정동 모두)
     */
    if (riderLocation.getStatus() == RiderStatus.OFFLINE) {
      String beforeAdminCode = redisTemplate.opsForValue().get(riderBeforeAdminCodeKey);

      // 이전 행정동에서 해당 라이더 삭제
      if (!Objects.isNull(beforeAdminCode) && !beforeAdminCode.isBlank()) {
        String riderBeforeAdminCodeLocationKey = ADMIN_CODE_RIDER_LOCATION.formatted(beforeAdminCode);
        redisTemplate.opsForGeo().remove(riderBeforeAdminCodeLocationKey,  String.valueOf(riderUserId));
      }

      // 현재 행정동에서 해당 라이더 삭제
      if (!Objects.isNull(currentAdminCode) && !currentAdminCode.isBlank()) {
        String riderCurrentAdminCodeLocationKey = ADMIN_CODE_RIDER_LOCATION.formatted(currentAdminCode);
        redisTemplate.opsForGeo().remove(riderCurrentAdminCodeLocationKey,  String.valueOf(riderUserId));
      }

      return;
    }

    /** AVAILABLE
     * 라이더가 배달 가능 상태로 변경하면,
     * 현재 행정동 위치와 이전 행정동 위치 조회
     * 이전 행정동 위치와 현재 행정동 위치가 다르면 이전 행정동의 set에서 제거
     * 현재 행정동 위치의 배달 가능 라이더로 업데이트
     */
    if (Objects.isNull(currentAdminCode) || currentAdminCode.isBlank()) {
      return;
    }

    /**
     * 주기적으로 오는 라이더들 위치 업데이트
     */
    String riderLocationKey = ADMIN_CODE_RIDER_LOCATION.formatted(currentAdminCode);

    String beforeAdminCode = redisTemplate.opsForValue().get(riderBeforeAdminCodeKey);
    if (currentAdminCode.equals(beforeAdminCode)) {
      redisTemplate.opsForGeo().add(riderLocationKey, new Point(longitude, latitude), String.valueOf(riderUserId));
      return;
    }

    // 이전 행정동 코드에서 해당 라이더 제거
    if (!Objects.isNull(beforeAdminCode) && !beforeAdminCode.isBlank()) {

      // 이전 행정동 GEO 에서 라이더 삭제
      String riderBeforeAdminCodeLocationKey = ADMIN_CODE_RIDER_LOCATION.formatted(beforeAdminCode);
      redisTemplate.opsForGeo().remove(riderBeforeAdminCodeLocationKey,  String.valueOf(riderUserId));
    }

    // 현재 행정동의 라이더 위치 추가
    redisTemplate.opsForGeo().add(riderLocationKey, new Point(longitude, latitude), String.valueOf(riderUserId));

    // 라이더 이전 행정동 위치를 현재 행정동 코드로 업데이트
    redisTemplate.opsForValue().set(riderBeforeAdminCodeKey, currentAdminCode);
  }
}
