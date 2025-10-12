package com.bappul.catalog.web.v1.controller.menu;

import com.bappul.catalog.application.service.MenuImageService;
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
@RequestMapping("/api/v1/menus")
public class MenuImageController {

  private final MenuImageService menuImageService;

  @GetMapping("/images/{menuImageId}/download")
  public ResponseEntity<Resource> downloadMenuImage(@PathVariable Long menuImageId) {
    ImageDownload imageDownload = menuImageService.downloadMenuImage(menuImageId);

    HttpHeaders headers = new HttpHeaders();
    headers.setETag(imageDownload.getETag());
    headers.setContentType(MediaType.valueOf(imageDownload.getContentType()));
    headers.setContentLength(imageDownload.getContentLength());
    headers.setContentDisposition(ContentDisposition.inline()
        .filename(imageDownload.getFileName(), StandardCharsets.UTF_8)
        .build());

    return new ResponseEntity<>(imageDownload.getResource(), headers, HttpStatus.OK);
  }

  @GetMapping("/{menuId}/thumbnail/download")
  public ResponseEntity<Resource> downloadThumbnail(@PathVariable Long menuId) {
    ImageDownload imageDownload = menuImageService.downloadThumbnailMenuImage(menuId);

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
