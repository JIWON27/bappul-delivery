package com.bappul.catalog.web.v1.controller.store;

import com.bappul.catalog.application.service.StoreImageService;
import com.bappul.image.service.ImageDownload;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stores")
public class StoreImageController {

  private final StoreImageService storeImageService;

  @GetMapping("/{storeId}/image/download")
  public ResponseEntity<Resource> downloadThumbnail(@PathVariable Long storeId) {
    ImageDownload imageDownload = storeImageService.downloadStoreImage(storeId);

    HttpHeaders headers = new HttpHeaders();
    headers.setETag(imageDownload.getETag());
    headers.setContentType(MediaType.valueOf(imageDownload.getContentType()));
    headers.setContentLength(imageDownload.getContentLength());
    headers.setContentDisposition(ContentDisposition.inline()
        .filename(imageDownload.getFileName(), StandardCharsets.UTF_8)
        .build());

    return new ResponseEntity<>(imageDownload.getResource(), headers, HttpStatus.OK);
  }

}
