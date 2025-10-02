package com.bappul.delivery.user.application.service;

import com.bappul.delivery.user.application.mapper.UserMapper;
import com.bappul.delivery.user.common.validator.UserValidator;
import com.bappul.delivery.user.domain.entity.user.User;
import com.bappul.delivery.user.domain.repository.UserRepository;
import com.bappul.delivery.user.web.dto.UserRequest;
import com.bappul.delivery.user.web.dto.UserResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
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

    return userMapper.toResponse(user);
  }

  @Transactional(readOnly = true)
  public UserResponse getUserById(Long userId) {
    User user = userValidator.getById(userId);
    return userMapper.toResponse(user);

  }

  @Transactional(readOnly = true)
  public UserResponse getUserByUuid(UUID uuid) {
    User user = userValidator.getByUuid(uuid);
    return userMapper.toResponse(user);
  }

  private String encodePassword(String rawPassword) {
    return passwordEncoder.encode(rawPassword);
  }

  private UUID generateUUID() {
    return UUID.randomUUID();
  }
}
