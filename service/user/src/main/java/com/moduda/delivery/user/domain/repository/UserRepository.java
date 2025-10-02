package com.moduda.delivery.user.domain.repository;

import com.moduda.delivery.user.domain.entity.user.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);
  Optional<User> findByNickname(String nickname);
  Optional<User> findByUuid(UUID uuid);
}
