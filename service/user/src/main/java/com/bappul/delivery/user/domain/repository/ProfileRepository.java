package com.bappul.delivery.user.domain.repository;

import com.bappul.delivery.user.domain.entity.user.Profile;
import com.bappul.delivery.user.domain.entity.user.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

  Optional<Profile> findByUser(User user);
}
