package com.bappul.delivery.user.application.service;

import com.bappul.delivery.user.application.mapper.UserMapper;
import com.bappul.delivery.user.common.validator.UserValidator;
import com.bappul.delivery.user.domain.entity.user.Profile;
import com.bappul.delivery.user.domain.entity.user.User;
import com.bappul.delivery.user.domain.repository.ProfileRepository;
import com.bappul.delivery.user.domain.repository.UserRepository;
import com.bappul.delivery.user.web.dto.UserRequest;
import com.bappul.delivery.user.web.dto.UserResponse;
import com.bappul.image.service.ImageDownload;
import com.bappul.image.service.ImageMeta;
import com.bappul.image.service.ImageService;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserService {

  private final ImageService imageService;
  private final UserRepository userRepository;
  private final ProfileRepository profileRepository;
  private final UserValidator userValidator;
  private final PasswordEncoder passwordEncoder;
  private final UserMapper userMapper;

  @Transactional
  public UserResponse join(UserRequest request) {
    userValidator.validateDuplicateEmail(request.getEmail());
    userValidator.validateDuplicateNickName(request.getNickname());

    UUID uuid = generateUUID();
    User user = userMapper.toEntity(request, uuid);
    String encodedPassword = encodePassword(request.getPassword());
    user.updatePassword(encodedPassword);
    userRepository.save(user);
    String profileDownloadUrl = generateProfileDownloadUrl(user.getId());
    return userMapper.toResponse(user, profileDownloadUrl);
  }

  @Transactional(readOnly = true)
  public UserResponse getUserById(Long userId) {
    User user = userValidator.getById(userId);
    String profileDownloadUrl = generateProfileDownloadUrl(user.getId());
    return userMapper.toResponse(user, profileDownloadUrl);
  }

  @Transactional(readOnly = true)
  public UserResponse getUserByUuid(UUID uuid) {
    User user = userValidator.getByUuid(uuid);
    String profileDownloadUrl = generateProfileDownloadUrl(user.getId());
    return userMapper.toResponse(user, profileDownloadUrl);
  }

  @Transactional
  public void updateProfile(MultipartFile file, Long userId) {
    User user = userValidator.getById(userId);

    String basePath = userId + "/";
    ImageMeta imageMeta = imageService.uploadImage(file, basePath);

    // 기존 프로필 조회
    Profile profile = profileRepository.findByUser(user).orElse(null);
    String oldStorageKey = (profile != null) ? profile.getStorageKey() : null;

    if (Objects.isNull(oldStorageKey)) {
      profile = Profile.builder()
          .user(user)
          .originalName(imageMeta.getOriginalImageName())
          .savedName(imageMeta.getSavedImageName())
          .storageKey(imageMeta.getStorageKey())
          .contentType(imageMeta.getContentType())
          .size(imageMeta.getSize())
          .build();
    } else {
      profile.updateOriginalName(imageMeta.getOriginalImageName());
      profile.updateSavedName(imageMeta.getSavedImageName());
      profile.updateStorageKey(imageMeta.getStorageKey());
      profile.updateContentType(imageMeta.getContentType());
      profile.updateSize(imageMeta.getSize());
    }

    profileRepository.save(profile);

    // TODO 이미지 트랜잭션 커밋 후 삭제하도록 수정
  }

  public ImageDownload downloadProfile(Long userId) {
    User user = userValidator.getById(userId);
    Profile profile = profileRepository.findByUser(user).orElse(null);

    String basePath = "default/";
    String fileName = "default.jpg";
    ImageDownload imageDownload = imageService.getImage(basePath, fileName);

    if (!Objects.isNull(profile)) {
      basePath = userId + "/";
      fileName = profile.getSavedName();
      imageDownload = imageService.getImage(basePath, fileName);
    }

    return imageDownload;
  }

  private String generateProfileDownloadUrl(Long userId){
    return "http://localhost:8000/api/v1/users/" + userId + "/profile/download";
  }

  private String encodePassword(String rawPassword) {
    return passwordEncoder.encode(rawPassword);
  }

  private UUID generateUUID() {
    return UUID.randomUUID();
  }
}
