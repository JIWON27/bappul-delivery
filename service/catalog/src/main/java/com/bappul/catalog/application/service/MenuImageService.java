package com.bappul.catalog.application.service;

import com.bappul.catalog.application.validator.MenuImageValidator;
import com.bappul.catalog.application.validator.MenuValidator;
import com.bappul.catalog.domain.entity.Menu;
import com.bappul.catalog.domain.entity.MenuImage;
import com.bappul.catalog.domain.repository.MenuImageRepository;
import com.bappul.image.service.ImageDownload;
import com.bappul.image.service.ImageMeta;
import com.bappul.image.service.ImageService;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MenuImageService {

  private final ImageService imageService;
  private final MenuImageRepository menuImageRepository;
  private final MenuValidator menuValidator;
  private final MenuImageValidator menuImageValidator;

  @Transactional
  public void uploadThumbnailMenuImage(MultipartFile file, Long menuId) {
    Menu menu = menuValidator.getMenu(menuId);

    String basePath = "menus/%d/".formatted(menuId);
    ImageMeta imageMeta = imageService.uploadImage(file, basePath);

    // 기존 썸네일 조회
    MenuImage thumbnail = menuImageRepository.findByMenuAndThumbnailTrue(menu).orElse(null);
    String oldStorageKey = (thumbnail != null) ? thumbnail.getStorageKey() : null;

    if (Objects.isNull(oldStorageKey)) {
      thumbnail = MenuImage.builder()
          .menu(menu)
          .originalName(imageMeta.getOriginalImageName())
          .savedName(imageMeta.getSavedImageName())
          .storageKey(imageMeta.getStorageKey())
          .contentType(imageMeta.getContentType())
          .size(imageMeta.getSize())
          .thumbnail(true)
          .build();
    } else {
      thumbnail.updateOriginalName(imageMeta.getOriginalImageName());
      thumbnail.updateSavedName(imageMeta.getSavedImageName());
      thumbnail.updateStorageKey(imageMeta.getStorageKey());
      thumbnail.updateContentType(imageMeta.getContentType());
      thumbnail.updateSize(imageMeta.getSize());
    }

    menuImageRepository.save(thumbnail);
    // TODO 이미지 트랜잭션 커밋 후 삭제하도록 수정
  }

  @Transactional
  public void upsertMenuImage(List<MultipartFile> files, Long menuId) {
    Menu menu = menuValidator.getMenu(menuId);
    List<MenuImage> menuImages = new ArrayList<>(files.size());

    for (MultipartFile file : files) {
      String basePath = "menus/%d/".formatted(menuId);
      ImageMeta imageMeta = imageService.uploadImage(file, basePath);

      MenuImage menuImage = MenuImage.builder()
          .menu(menu)
          .originalName(imageMeta.getOriginalImageName())
          .savedName(imageMeta.getSavedImageName())
          .storageKey(imageMeta.getStorageKey())
          .contentType(imageMeta.getContentType())
          .size(imageMeta.getSize())
          .thumbnail(false)
          .build();
      menuImages.add(menuImage);
    }

    menuImageRepository.saveAll(menuImages);
    // TODO 기존 메뉴 이미지는 커밋 완료 후 삭제
  }

  public ImageDownload downloadThumbnailMenuImage(Long menuId) {
    Menu menu = menuValidator.getMenu(menuId);
    MenuImage menuImage = menuImageValidator.getThumbnailImage(menu);

    // TODO storageKey 사용하도록 수정
    String basePath = "/menus/%d/".formatted(menuImage.getMenu().getId());
    String fileName = menuImage.getSavedName();

    return imageService.getImage(basePath, fileName);
  }

  public ImageDownload downloadMenuImage(Long menuImageId) {
    MenuImage menuImage = menuImageValidator.getMenuImage(menuImageId);

    // TODO storageKey 사용하도록 수정
    String basePath = "/menus/%d/".formatted(menuImage.getMenu().getId());
    String fileName = menuImage.getSavedName();

    return imageService.getImage(basePath, fileName);
  }

  public List<String> getMenuImageUrls(Menu menu) {
    List<MenuImage> menuImages = menuImageRepository.findByMenuAndThumbnailFalse(menu);
    List<String> imageUrls = new ArrayList<>();
    for (MenuImage menuImage : menuImages) {
      String imageUrl = getMenuImageUrl(menuImage.getId());
      imageUrls.add(imageUrl);
    }
    return imageUrls;
  }

  public String getThumbnailUrl(Long menuId) {
    return "http://localhost:8000/api/v1/menus/%d/thumbnail/download".formatted(menuId);
  }

  private String getMenuImageUrl(Long menuImageId){
    return "http://localhost:8000/api/v1/menus/images/" + menuImageId + "/download";
  }

}
