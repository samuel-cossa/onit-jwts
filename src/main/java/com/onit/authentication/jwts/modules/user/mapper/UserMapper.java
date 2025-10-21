package com.onit.authentication.jwts.modules.user.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.onit.authentication.jwts.modules.user.domain.User;
import com.onit.authentication.jwts.modules.user.dto.UserRequestDto;
import com.onit.authentication.jwts.modules.user.dto.UserResponseDto;

/**
 * UserMapper
 *
 * @author Samuel Cossa <a href="https://github.com/samuel-cossa">...</a>
 * @version 1.0
 * @email ar.sam.cossa@gmail.com.com
 * @license MIT
 * @since 10/20/25
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Map from User Entity to UserResponseDto.
     */
    @Mapping(target = "roles", expression = "java(mapRoles(user))")
    UserResponseDto toUserResponseDto(User user);

    /** Get User Role list and map it from a Set UserRole entity to Set of String (Role.getName() as String) */
    default Set<String> mapRoles(User user) {
        if (user.getRoles() == null) {
            return Set.of();
        }
        return user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());
    }

    /**
     * Map from UserRequestDto to User Entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "modifier", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    User toUserEntity(UserRequestDto dto);
}
