package com.bappul.delivery.user.common.validator;

import static com.bappul.delivery.user.common.exception.ServiceExceptionCode.DUPLICATE_EMAIL;
import static com.bappul.delivery.user.common.exception.ServiceExceptionCode.DUPLICATE_NICKNAME;
import static com.bappul.delivery.user.common.exception.ServiceExceptionCode.NOT_FOUND_USER;

import com.bappul.delivery.user.domain.entity.user.User;
import com.bappul.delivery.user.domain.repository.UserRepository;
import exception.ServiceException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserValidator {
  private final UserRepository userRepository;

  public User getById(Long userId){
    return userRepository.findById(userId)
        .orElseThrow(() -> new ServiceException(NOT_FOUND_USER));
  }

  public User getByUuid(UUID uuid){
    return userRepository.findByUuid(uuid)
        .orElseThrow(() -> new ServiceException(NOT_FOUND_USER));

  }

  public void validateDuplicateEmail(String email) {
    userRepository.findByEmail(email).ifPresent((user) -> {
      throw new ServiceException(DUPLICATE_EMAIL);
    });
  }

  public void validateDuplicateNickName(String nickname){
    userRepository.findByNickname(nickname).ifPresent((user) -> {
      throw new ServiceException(DUPLICATE_NICKNAME);
    });
  }


}

