package com.onit.authentication.jwts.common.dto;

import java.util.Set;

public record AuthenticatedUserResponse(
    String name,
    String email,
    Set<String> roles) {
}

