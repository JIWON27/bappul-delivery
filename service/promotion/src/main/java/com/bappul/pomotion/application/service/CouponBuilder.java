package com.bappul.pomotion.application.service;

import com.bappul.pomotion.application.mapper.CouponMapper;
import com.bappul.pomotion.application.validator.CouponValidator;
import com.bappul.pomotion.domain.entity.Coupon;
import com.bappul.pomotion.domain.entity.CouponPolicy;
import com.bappul.pomotion.domain.entity.CouponStatus;
import com.bappul.pomotion.domain.entity.CouponType;
import com.bappul.pomotion.domain.repository.CouponRepository;
import com.bappul.pomotion.web.v1.request.CouponCreateRequest;
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
  private final CouponMapper couponMapper;
  private final ExpirationCalculator expirationCalculator;

  private static final String CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  private static final int CODE_LENGTH = 40;

  public List<Coupon> generateCoupons(CouponPolicy policy, CouponCreateRequest request){
    int quantity = request.getQuantity();
    int issuedQuantity = policy.getIssuedQuantity();
    couponValidator.validateCouponIssueLimit(issuedQuantity, quantity, policy.getTotalQuantity());

    List<Coupon> coupons = new ArrayList<>(quantity);
    List<String> codes = generateUniqueCodes(quantity, request.getPrefix());

    LocalDateTime expiresAt = expirationCalculator.computeExpiresAt(policy);

    for (String code : codes) {
      Coupon coupon = couponMapper.toCoupon(null, policy, CouponStatus.CREATED, request.getType(), code, expiresAt);
      coupons.add(coupon);
    }

    return coupons;
  }

  public Coupon generateCoupon(CouponPolicy policy, Long userId){
    LocalDateTime expiresAt = expirationCalculator.computeExpiresAt(policy);
    return couponMapper.toCoupon(userId, policy, CouponStatus.CREATED, CouponType.ONLINE, null, expiresAt);
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
