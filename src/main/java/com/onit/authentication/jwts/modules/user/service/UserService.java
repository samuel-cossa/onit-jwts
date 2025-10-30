package com.onit.authentication.jwts.modules.user.service;

import java.util.List;

import com.onit.authentication.jwts.common.dto.StatusResponseDto;
import com.onit.authentication.jwts.modules.user.dto.UserLoginDto;
import com.onit.authentication.jwts.modules.user.dto.UserRequestDto;
import com.onit.authentication.jwts.modules.user.dto.UserResponseDto;
import com.onit.authentication.jwts.security.model.CustomUserPrincipal;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.security.core.Authentication;

public interface UserService {
    UserResponseDto findById(Long id);

    UserResponseDto findByEmail(String email);

    UserResponseDto createUser(UserRequestDto requestDto);

    UserResponseDto updateUser(UserRequestDto requestDto);

    List<UserResponseDto> findAll();

    UserResponseDto signUp(UserRequestDto responseDto, HttpServletResponse response);

    UserResponseDto logIn(UserLoginDto loginDto, HttpServletResponse response);

    CustomUserPrincipal singIn(UserLoginDto loginDto, HttpServletResponse response);

    StatusResponseDto isUserExists(String email);
}
