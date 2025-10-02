package com.moduda.delivery.user.application.mapper;

import com.moduda.delivery.user.domain.entity.user.User;
import com.moduda.delivery.user.web.dto.UserRequest;
import com.moduda.delivery.user.web.dto.UserResponse;
import java.util.UUID;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

  UserResponse toResponse(User user);
  User toEntity(UserRequest userRequest, UUID uuid);
}
