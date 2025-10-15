package com.bappul.pomotion.application.utils;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TimeUtils {

  private final Clock clock;

  public LocalDateTime now() {
    return LocalDateTime.now(clock);
  }

  public LocalDate today() {
    return LocalDate.now(clock);
  }
}
