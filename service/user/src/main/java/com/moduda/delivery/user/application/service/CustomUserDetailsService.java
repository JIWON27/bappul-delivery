package com.moduda.delivery.user.application.service;

import com.moduda.delivery.user.common.jwt.CustomUserDetails;
import com.moduda.delivery.user.domain.entity.user.User;
import com.moduda.delivery.user.domain.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;
  private final static String NOT_FOUND_USER = "존재하지 않는 사용자입니다.";

  public CustomUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException(NOT_FOUND_USER));
    return CustomUserDetails.from(user);
  }
}
