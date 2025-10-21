package com.onit.authentication.jwts.modules.user.service;

import java.util.List;

import com.onit.authentication.jwts.modules.user.dto.UserRequestDto;
import com.onit.authentication.jwts.modules.user.dto.UserResponseDto;

public interface UserService {
    UserResponseDto findById(Long id);

    UserResponseDto findByEmail(String email);

    UserResponseDto createUser(UserRequestDto requestDto);

    UserResponseDto updateUser(UserRequestDto requestDto);

    List<UserResponseDto> findAll();
}
