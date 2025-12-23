package com.pizzaro_go.user.mapper;

import com.pizzaro_go.user.dtos.UserRequest;
import com.pizzaro_go.user.dtos.UserResponse;
import com.pizzaro_go.user.entity.User;
import com.pizzaro_go.common.enums.Role;


import org.mapstruct.*;

/**
 * Mapper for converting between User entities and User DTOs.
 * Uses MapStruct to generate the implementation.
 */
@Mapper(componentModel = "spring", imports = { Role.class })
public interface IUserMapper {

    /**
     * Converts a UserRequest into a User entity.
     *
     * @param request the incoming user data
     * @return the mapped User entity
     */
    @Mapping(target = "role",
            expression = "java(request.getRole() != null ? Role.valueOf(request.getRole().toUpperCase()) : Role.CUSTOMER)")
    @Mapping(target = "orders", ignore = true)
    User toEntity(UserRequest request);


    /**
     * Converts a User entity into a UserResponse.
     *
     * @param user the User entity to convert
     * @return the mapped UserResponse
     */
    @Mapping(target = "role", expression = "java(user.getRole().name())")
    UserResponse toResponse(User user);
}
