package com.bappul.catalog.web.v1.controller.menu;

import com.bappul.catalog.application.service.MenuImageService;
import com.bappul.catalog.application.service.MenuService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/owner/menus")
public class MenuOwnerController {

  private final MenuService menuService;
  private final MenuImageService menuImageService;

  @DeleteMapping("/{menuId}")
  public ResponseEntity<Void> deleteById(@PathVariable Long menuId) {
    menuService.deleteById(menuId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @PatchMapping("/{menuId}")
  public ResponseEntity<Void> setMenuSoldOut(
      @PathVariable Long menuId,
      @RequestParam("soldOut") boolean soldOut,
      @AuthenticationPrincipal(expression = "claims['uid']") String ownerId)
  {
    menuService.setMenuSoldOut(menuId, soldOut, Long.valueOf(ownerId));
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @PatchMapping("/options/{menuOptionValueId}")
  public ResponseEntity<Void> setMenuOptionSoldOut(
      @PathVariable Long menuOptionValueId,
      @RequestParam("soldOut") boolean soldOut,
      @AuthenticationPrincipal(expression = "claims['uid']") String ownerId)
  {
    menuService.setMenuOptionSoldOut(menuOptionValueId, soldOut, Long.valueOf(ownerId));
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }


  @PostMapping(value = "/{menuId}/thumbnail", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Void> updateThumbnailImage(
      @PathVariable Long menuId,
      @RequestParam("thumbnail") MultipartFile file)
  {
    menuImageService.uploadThumbnailMenuImage(file, menuId);
    return  ResponseEntity.status(HttpStatus.OK).build();
  }

  @PostMapping(value = "/{menuId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Void> upsertMenuImage(
      @RequestParam("images") List<MultipartFile> file,
      @PathVariable Long menuId)
  {
    menuImageService.upsertMenuImage(file, menuId);
    return  ResponseEntity.status(HttpStatus.OK).build();
  }
}
