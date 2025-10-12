package com.bappul.catalog.application.service;

import com.bappul.catalog.application.validator.StoreImageValidator;
import com.bappul.catalog.application.validator.StoreValidator;
import com.bappul.catalog.domain.entity.Store;
import com.bappul.catalog.domain.entity.StoreImage;
import com.bappul.catalog.domain.repository.StoreImageRepository;
import com.bappul.image.service.ImageDownload;
import com.bappul.image.service.ImageMeta;
import com.bappul.image.service.ImageService;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class StoreImageService {

  private final ImageService imageService;

  private final StoreImageRepository storeImageRepository;

  private final StoreImageValidator storeImageValidator;
  private final StoreValidator storeValidator;

  @Transactional
  public void uploadStoreImage(MultipartFile file, Long storeId) {
    Store store = storeValidator.getStore(storeId);

    String basePath = "stores/%d/".formatted(storeId);
    ImageMeta imageMeta = imageService.uploadImage(file, basePath);

    // 기존 썸네일 조회
    StoreImage storeImage = storeImageRepository.findByStore(store).orElse(null);
    String oldStorageKey = (storeImage != null) ? storeImage.getStorageKey() : null;

    if (Objects.isNull(oldStorageKey)) {
      storeImage = StoreImage.builder()
          .store(store)
          .originalName(imageMeta.getOriginalImageName())
          .savedName(imageMeta.getSavedImageName())
          .storageKey(imageMeta.getStorageKey())
          .contentType(imageMeta.getContentType())
          .size(imageMeta.getSize())
          .build();
    } else {
      storeImage.updateOriginalName(imageMeta.getOriginalImageName());
      storeImage.updateSavedName(imageMeta.getSavedImageName());
      storeImage.updateStorageKey(imageMeta.getStorageKey());
      storeImage.updateContentType(imageMeta.getContentType());
      storeImage.updateSize(imageMeta.getSize());
    }

    storeImageRepository.save(storeImage);
    // TODO 이미지 트랜잭션 커밋 후 삭제하도록 수정
  }

  public ImageDownload downloadStoreImage(Long storeId) {
    Store store = storeValidator.getStore(storeId);
    StoreImage storeImage = storeImageValidator.getStoreImage(store);

    // TODO storageKey 사용하도록 수정
    String basePath = "/stores/%d/".formatted(storeId);
    String fileName = storeImage.getSavedName();

    return imageService.getImage(basePath, fileName);
  }

  public String getStoreImageUrl(Long storeId){
    return "http://localhost:8000/api/v1/stores/%d/image/download".formatted(storeId);
  }

}
