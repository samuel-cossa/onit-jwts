package com.onit.authentication.jwts.common.enums;

import lombok.Getter;

@Getter
public enum RoleName {
    ROLE_ADMIN,
    ROLE_USER,
    ROLE_OWNER,
    ROLE_CREATOR,
    ROLE_FAN;

    @Override
    public String toString() {
        return name().replace("ROLE_", "");
    }
}