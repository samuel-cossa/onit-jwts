package com.onit.authentication.jwts.modules.user.service;

import java.util.Arrays;

import org.springframework.stereotype.Service;

import com.onit.authentication.jwts.common.enums.RoleName;
import com.onit.authentication.jwts.modules.user.domain.Role;
import com.onit.authentication.jwts.modules.user.repository.RoleRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RoleSeeder {

    private final RoleRepository roleRepository;

    @PostConstruct
    public void initRoles() {
        Arrays.stream(RoleName.values())
                .forEach(roleName -> roleRepository.findByName(roleName)
                        .ifPresentOrElse(
                                role -> {
                                    /* Role já existe - não faz nada */ },
                                () -> createNewRole(roleName)));
    }

    /* Save the newest Role to Database */
    private void createNewRole(RoleName roleName) {
        final var role = new Role();
        role.setName(roleName);
        roleRepository.save(role);
    }
}
