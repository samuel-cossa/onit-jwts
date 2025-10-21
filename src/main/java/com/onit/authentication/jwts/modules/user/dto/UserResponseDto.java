package com.onit.authentication.jwts.modules.user.dto;

import java.time.LocalDateTime;
import java.util.Set;

public record UserResponseDto(
        Long id,
        String name,
        String email,
        String password,
        Set<String> roles,
        LocalDateTime createdDate,
        LocalDateTime lastModifiedDate) {
}
