package com.bappul.catalog.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "menu_image")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MenuImage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "menu_id", nullable = false)
  Menu menu;

  @Column(name = "original_name", nullable = false)
  String originalName;

  @Column(name = "saved_name", nullable = false)
  String savedName;

  @Column(name = "storage_key", nullable = false)
  String storageKey;

  @Column(name = "content_type", nullable = false)
  String contentType;

  @Column(name = "size", nullable = false)
  Long size;

  @Column(name = "thumbnail", nullable = false)
  Boolean thumbnail;

  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  LocalDateTime createdAt;

  @UpdateTimestamp
  LocalDateTime updatedAt;

  @Builder
  public MenuImage(Menu menu, String originalName, String savedName, String storageKey,
      String contentType, Long size, Boolean thumbnail) {
    this.menu = menu;
    this.originalName = originalName;
    this.savedName = savedName;
    this.storageKey = storageKey;
    this.contentType = contentType;
    this.size = size;
    this.thumbnail = thumbnail;
  }

  public void updateOriginalName(String originalName) {
    this.originalName = originalName;
  }

  public void updateSavedName(String savedName) {
    this.savedName = savedName;
  }

  public void updateStorageKey(String storageKey) {
    this.storageKey = storageKey;
  }

  public void updateContentType(String contentType) {
    this.contentType = contentType;
  }

  public void updateSize(Long size) {
    this.size = size;
  }
}
