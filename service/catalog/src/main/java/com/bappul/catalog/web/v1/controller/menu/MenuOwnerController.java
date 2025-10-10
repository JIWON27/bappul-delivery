package com.bappul.catalog.web.v1.controller.menu;

import com.bappul.catalog.application.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/owner/menus")
public class MenuOwnerController {

  private final MenuService menuService;

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
}
