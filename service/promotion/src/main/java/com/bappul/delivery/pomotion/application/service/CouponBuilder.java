package com.bappul.delivery.pomotion.application.service;

import com.bappul.delivery.pomotion.application.validator.CouponValidator;
import com.bappul.delivery.pomotion.domain.entity.Coupon;
import com.bappul.delivery.pomotion.domain.entity.CouponPolicy;
import com.bappul.delivery.pomotion.domain.entity.CouponStatus;
import com.bappul.delivery.pomotion.domain.entity.CouponType;
import com.bappul.delivery.pomotion.domain.repository.CouponRepository;
import com.bappul.delivery.pomotion.web.v1.request.CouponCreateRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponBuilder {

  private final CouponRepository couponRepository;
  private final CouponValidator couponValidator;
  private final ExpirationCalculator expirationCalculator;

  private static final String CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  private static final int CODE_LENGTH = 40;

  public List<Coupon> generateCoupons(CouponPolicy policy, CouponCreateRequest request){
    int issuedQuantity = policy.getIssuedQuantity();
    couponValidator.validateCouponIssueLimit(issuedQuantity, request.getQuantity(), policy.getTotalQuantity());

    List<Coupon> coupons = new ArrayList<>(request.getQuantity());
    List<String> codes = generateUniqueCodes(request.getQuantity(), request.getPrefix());

    LocalDateTime expiresAt = expirationCalculator.getExpiresAt(policy);

    for (String code : codes) {
      Coupon coupon = Coupon.builder()
          .userId(null)
          .couponPolicy(policy)
          .status(CouponStatus.CREATED)
          .type(request.getType())
          .code(code)
          .expiresAt(expiresAt)
          .issuedAt(LocalDateTime.now())
          .build();
      coupons.add(coupon);
    }

    return coupons;
  }

  public Coupon generateCoupon(CouponPolicy policy, Long userId){
    LocalDateTime expiresAt = expirationCalculator.getExpiresAt(policy);
    return Coupon.builder()
        .userId(userId)
        .couponPolicy(policy)
        .status(CouponStatus.CREATED)
        .type(CouponType.ONLINE)
        .code(null)
        .expiresAt(expiresAt)
        .issuedAt(LocalDateTime.now())
        .build();
  }

  private List<String> generateUniqueCodes(int number, String prefix){
    Set<String> candidateCodes = new HashSet<>();

    while (candidateCodes.size() < number) {
      String code = generateCouponCode(prefix);
      candidateCodes.add(code);
    }

    List<String> existingCodes = couponRepository.findExistingCodes(candidateCodes);
    existingCodes.forEach(candidateCodes::remove);

    if (candidateCodes.size() < number) {
      candidateCodes.addAll(generateUniqueCodes(number - candidateCodes.size(), prefix));
    }
    return new ArrayList<>(candidateCodes);
  }

  private String generateCouponCode(String prefix){
    StringBuilder sb = new StringBuilder(prefix).append("-");
    ThreadLocalRandom random = ThreadLocalRandom.current();
    for (int i = 0; i < CODE_LENGTH; i++) {
      int index = random.nextInt(CHAR_POOL.length());
      sb.append(CHAR_POOL.charAt(index));
    }
    return sb.toString();
  }
}
