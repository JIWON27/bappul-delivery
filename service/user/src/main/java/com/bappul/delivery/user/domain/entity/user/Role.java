package com.bappul.delivery.user.domain.entity.user;

import lombok.Getter;

@Getter
public enum Role {
  USER("회원"),
  OWNER("점수"),
  ADMIN("관리자");

  private final String description;

  Role(String description) { this.description = description; }

  public String asAuthority() {
    return "ROLE_" + name();
  }
}
