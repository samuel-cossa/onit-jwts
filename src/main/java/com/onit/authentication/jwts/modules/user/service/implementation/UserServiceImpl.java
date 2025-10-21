package com.onit.authentication.jwts.modules.user.service.implementation;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.onit.authentication.jwts.common.exception.ResourceNotFoundException;
import com.onit.authentication.jwts.modules.user.domain.User;
import com.onit.authentication.jwts.modules.user.dto.UserRequestDto;
import com.onit.authentication.jwts.modules.user.dto.UserResponseDto;
import com.onit.authentication.jwts.modules.user.mapper.UserMapper;
import com.onit.authentication.jwts.modules.user.repository.UserRepository;
import com.onit.authentication.jwts.modules.user.service.UserService;

import lombok.RequiredArgsConstructor;

/**
 * Implementation of the UserService Interface
 * 
 * @author Samuel Cossa https://github.com/samuel-cossa
 */
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    final UserRepository userRepository;
    final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto findById(Long id) {
        return userRepository.findById(id).map(userMapper::toUserResponseDto)
                .orElseThrow(() -> new ResourceNotFoundException("User not found With ID = " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto findByEmail(String email) {
        return userRepository.findByEmail(email).map(userMapper::toUserResponseDto)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with emil =" + email));
    }

    @Override
    @Transactional
    public UserResponseDto createUser(UserRequestDto requestDto) {
        /* Busca ou cria usuário por email, se nao existir criar novo */
        final var user = userRepository.findByEmail(requestDto.email())
                .map(existingUser -> updateInfo(existingUser,
                        requestDto))
                .orElseGet(() -> userMapper.toUserEntity(requestDto));

        return userMapper.toUserResponseDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponseDto updateUser(UserRequestDto requestDto) {
        /* Busca ou cria usuário por id e email se nao existir criar novo */
        final var user = userRepository.findByIdAndEmail(requestDto.id(), requestDto.email())
                .map(existingUser -> updateInfo(existingUser,
                        requestDto))
                .orElseGet(() -> userMapper.toUserEntity(requestDto));

        return userMapper.toUserResponseDto(userRepository.save(user));
    }

    /* Overriding existing User with the updated info */
    private User updateInfo(User user, UserRequestDto requestDto) {
        user.setName(requestDto.name());
        user.setPassword(requestDto.password());
        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toUserResponseDto)
                .toList();
    }

}
