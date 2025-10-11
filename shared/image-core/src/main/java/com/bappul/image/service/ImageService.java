package com.bappul.image.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
  ImageMeta uploadImage(MultipartFile file, String basePath);
  ImageDownload getImage(String basePath, String fileName);
  void deleteImage(String basePath);
}
