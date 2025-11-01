package com.bappul.web.v1.response;

import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KakaoLocalResponse {
  Meta meta;
  List<Document> documents;

  @Getter
  @Builder
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class Meta {
    Integer total_count;
    Integer pageable_count;
    Boolean is_end;
  }

  @Getter
  @Builder
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class Document {
    String address_name;
    String y;                 // 위도 (문자열로 옴)
    String x;                 // 경도 (문자열로 옴)
    AddressType address_type;
    Address address;
    RoadAddress road_address;
  }

  @Getter
  @Builder
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class Address {
    String address_name;
    String region_1depth_name;
    String region_2depth_name;
    String region_3depth_name;
    String region_3depth_h_name;
    String h_code;            // 행정동 코드
    String b_code;            // 법정동 코드
    String mountain_yn;
    String main_address_no;
    String sub_address_no;
    String x;                 // 경도
    String y;                 // 위도
  }

  @Getter
  @Builder
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class RoadAddress {
    String address_name;
    String region_1depth_name;
    String region_2depth_name;
    String region_3depth_name;
    String road_name;
    String underground_yn;
    String main_building_no;
    String sub_building_no;
    String building_name;
    String zone_no;           // 우편번호
    String y;                 // 위도
    String x;                 // 경도
  }
}
