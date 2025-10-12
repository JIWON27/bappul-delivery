package com.bappul.image.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;

public class LocalImageService implements ImageService {

  @Value("${image.root.path}")
  private String imageRootPath;

  private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
      "image/jpeg", "image/png", "image/webp"
  );

  @Override
  public ImageMeta uploadImage(MultipartFile file, String basePath) {
    try {
      validateFile(file);
      Path rootDir = Files.createDirectories(Path.of(imageRootPath).normalize());

      String ext = getFileExtension(file.getOriginalFilename());
      String saveImageName = generateImageName(ext);
      Path saveDir = Files.createDirectories(rootDir.resolve(basePath).normalize());
      Path fullPath = saveDir.resolve(saveImageName);
      String storageKey = basePath + saveImageName;

      file.transferTo(fullPath);

      return ImageMeta.builder()
          .originalImageName(file.getOriginalFilename())
          .savedImageName(saveImageName)
          .storageKey(storageKey)
          .contentType(file.getContentType())
          .size(file.getSize())
          .build();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public List<ImageMeta> uploadImages(List<MultipartFile> file, String basePath) {
    try {
      List<ImageMeta> imageMetas = new ArrayList<>();
      for (MultipartFile fileItem : file) {
        ImageMeta imageMeta = uploadImage(fileItem, basePath);
        imageMetas.add(imageMeta);
      }
      return imageMetas;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public ImageDownload getImage(String basePath, String fileName) {
    try {
      Path target = Path.of(imageRootPath, basePath, fileName).normalize();
      String contentType = Files.probeContentType(target);
      long size = Files.size(target);
      String eTag = "\"" + Files.getLastModifiedTime(target).toMillis() + "-" + size + "\"";
      UrlResource resource = new UrlResource(target.toUri());
      return ImageDownload.builder()
          .fileName(fileName)
          .contentLength(size)
          .contentType(contentType)
          .eTag(eTag)
          .resource(resource)
          .build();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void deleteImage(String imageFullPath) {
    boolean delete = new File(imageFullPath).delete();
    if  (!delete) {
      throw new RuntimeException();
    }
  }

  private String getFileExtension(String fileName) {
    return fileName.substring(fileName.lastIndexOf("."));
  }

  private void validateFile(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new RuntimeException("빈 파일입니다."); // TODO 예외 고민
    }
    String contentType = file.getContentType();
    if (!ALLOWED_CONTENT_TYPES.contains(contentType)) {
      throw new RuntimeException("허용되지 않은 콘텐츠 타입: " + contentType);
    }
    // TODO : 크기 제한 등
  }

  private String generateImageName(String ext){
    return UUID.randomUUID().toString().replace("-", "")+ext;
  }
}
