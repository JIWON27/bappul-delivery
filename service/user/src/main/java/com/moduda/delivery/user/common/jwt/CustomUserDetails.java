package com.moduda.delivery.user.common.jwt;

import com.moduda.delivery.user.domain.entity.user.User;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class CustomUserDetails implements UserDetails {
  private final Long userId;
  private final UUID uuid;
  private final String email;
  private final String password;
  private final Collection<? extends GrantedAuthority> authorities;

  public CustomUserDetails(Long userId, UUID uuid, String email, String password,
      Collection<? extends GrantedAuthority> authorities) {
    this.userId = userId;
    this.uuid = uuid;
    this.email = email;
    this.password = password;
    this.authorities = authorities;
  }

  public static CustomUserDetails from(User user) {
    List<SimpleGrantedAuthority> auths = List.of(
        new SimpleGrantedAuthority(user.getRole().name()));
    return new CustomUserDetails(user.getId(), user.getUuid(), user.getEmail(), user.getPassword(), auths);
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

}
