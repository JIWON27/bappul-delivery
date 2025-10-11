package com.bappul.image.service;

import lombok.Builder;
import lombok.Getter;
import org.springframework.core.io.Resource;

@Getter
public class ImageDownload {
  String fileName;
  String contentType;
  long contentLength;
  String eTag;
  Resource resource;

  @Builder
  public ImageDownload(String fileName, String contentType, long contentLength, String eTag, Resource resource) {
    this.fileName = fileName;
    this.contentType = contentType;
    this.contentLength = contentLength;
    this.eTag = eTag;
    this.resource = resource;
  }
}
