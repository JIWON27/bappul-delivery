package com.bappul.pomotion.application.service;

import com.bappul.pomotion.application.utils.TimeUtils;
import com.bappul.pomotion.domain.entity.Coupon;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DDayCalculator {

  private final TimeUtils timeUtils;

  public int calculateDDay(Coupon coupon) {
    LocalDate today = timeUtils.today();
    LocalDateTime expiresAt = coupon.getExpiresAt();

    if (Objects.isNull(expiresAt)) {
      return 0;
    }

    if (isExpired(expiresAt)) {
      return 0;
    }

    int dDay = (int) ChronoUnit.DAYS.between(today, expiresAt.toLocalDate());
    return Math.max(0, dDay);
  }

  public boolean isExpired(LocalDateTime expiresAt) {
    if (Objects.isNull(expiresAt))  {
      return false;
    }
    return !timeUtils.now().isBefore(expiresAt);
  }

}
