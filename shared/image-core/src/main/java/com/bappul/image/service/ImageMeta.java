package com.bappul.image.service;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ImageMeta {
  String originalImageName;
  String savedImageName;
  String storageKey;
  String contentType;
  long size;

  @Builder
  public ImageMeta(String originalImageName, String savedImageName, String storageKey,
      String contentType, long size) {
    this.originalImageName = originalImageName;
    this.savedImageName = savedImageName;
    this.storageKey = storageKey;
    this.contentType = contentType;
    this.size = size;
  }
}
