package com.moduda.delivery.user.domain.entity.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "users")
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE users SET withdrawn_at = NOW() WHERE id = ?")
@SQLRestriction("withdrawn_at IS NULL")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(name = "uuid", nullable = false)
  UUID uuid;

  @Column(nullable = false, unique = true)
  String email;

  @Column(name = "nickname")
  String nickname;

  @Column(name = "password", nullable = false)
  String password;

  @Column(name = "phone")
  String phone;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  Gender gender;

  @Column(name = "birth_date")
  LocalDate birthDate;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  Role role;

  @Column(name = "is_verified")
  Boolean isVerified;

  @Column(name = "is_active")
  Boolean isActive;

  @Column(name = "last_login_at")
  LocalDateTime lastLoginAt;

  @Column(name = "last_password_change_at")
  LocalDateTime lastPasswordChangeAt;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  LocalDateTime updatedAt;

  @Column(name = "withdrawn_at")
  LocalDateTime withdrawnAt;

  @Builder
  public User(UUID uuid, String email, String nickname, String password, String phone,
      Gender gender, LocalDate birthDate, Role role, Boolean isVerified, Boolean isActive) {
    this.uuid = uuid;
    this.email = email;
    this.nickname = nickname;
    this.password = password;
    this.phone = phone;
    this.gender = gender;
    this.birthDate = birthDate;
    this.role = role;
    this.isVerified = isVerified;
    this.isActive = isActive;
  }

  public void updatePassword(String password) {
    this.password = password;
  }
}
