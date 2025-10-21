package com.onit.authentication.jwts.modules.user.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * UserLoginDto
 *
 * @author Samuel Cossa <a href="https://github.com/samuel-cossa">...</a>
 * @version 1.0
 * @email ar.sam.cossa@gmail.com.com
 * @license MIT
 * @since 10/20/25
 */
public record UserLoginDto(
        @NotBlank String email,
        @NotBlank String password
) {
}
