package com.ecommerce.mapper;

import com.ecommerce.dto.UserRequest;
import com.ecommerce.dto.UserResponse;
import com.ecommerce.model.entity.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserRequest userRequest);

    UserResponse toDto(User user);

    List<UserResponse> toDtoList(List<User> users);
}
