package com.moduda.delivery.catalog.application.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MenuImageService {

  /**
   * [TODO] 메뉴 이미지 CRUD 구현
   */

  public String saveImage(MultipartFile image){
    // 이미지 S3 or Local에 저장 후 저장한 위치 경로 or URL을 반환 로직 추가 예정
    return UUID.randomUUID().toString();
  }
}
