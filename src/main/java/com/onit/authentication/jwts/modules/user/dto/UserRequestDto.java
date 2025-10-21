package com.onit.authentication.jwts.modules.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRequestDto(
        Long id,
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank String password) {

}
