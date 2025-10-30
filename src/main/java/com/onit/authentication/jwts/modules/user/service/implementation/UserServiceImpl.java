package com.onit.authentication.jwts.modules.user.service.implementation;

import java.util.List;
import java.util.Set;

import com.onit.authentication.jwts.common.dto.StatusResponseDto;
import com.onit.authentication.jwts.common.enums.RoleName;
import com.onit.authentication.jwts.modules.user.domain.Role;
import com.onit.authentication.jwts.modules.user.dto.UserLoginDto;
import com.onit.authentication.jwts.modules.user.repository.RoleRepository;
import com.onit.authentication.jwts.security.jwt.JwtTokenProvider;
import com.onit.authentication.jwts.security.model.CustomUserPrincipal;
import com.onit.authentication.jwts.security.refresh.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
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
 * @author Samuel Cossa <a href="https://github.com/samuel-cossa">...</a>
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

  final AuthenticationManager authenticationManager;
  final UserRepository userRepository;
  final RoleRepository roleRepository;
  final PasswordEncoder passwordEncoder;
  final RefreshTokenService refreshTokenService;
  final JwtTokenProvider jwtTokenProvider;
  final UserMapper userMapper;
  @Value("${app.environment:dev}")
  private String environment;
  private static final String ACCESS_TOKEN ="accessToken";
  private static final String REFRESH_TOKEN ="refreshToken";

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

        final var user = userRepository.findByEmail(requestDto.email())
                .map(existingUser -> updateInfo(existingUser,
                        requestDto))
                .orElseGet(() -> userMapper.toUserEntity(requestDto));

            user.setPassword(passwordEncoder.encode(user.getPassword()));
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

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toUserResponseDto)
                .toList();
    }

  @Override
  public UserResponseDto signUp(@Valid  UserRequestDto requestDto, @NonNull HttpServletResponse response) {

      final var mapedUser = userRepository.findByEmail(requestDto.email())
        .map(existingUser -> updateInfo(existingUser,
          requestDto))
        .orElseGet(() -> userMapper.toUserEntity(requestDto));

      var defaultRoles = getDefaultRoles(mapedUser.getEmail(),mapedUser);

      mapedUser.setRoles(defaultRoles);
      mapedUser.setPassword(passwordEncoder.encode(mapedUser.getPassword()));
      final var newUSer = userRepository.save(mapedUser);
      final var accessToken = jwtTokenProvider.generateToken(newUSer);
      final var refreshToken = refreshTokenService.createRefreshToken(newUSer);
      final var isDev = environment.equalsIgnoreCase("dev");

      response.addCookie(createCookie(ACCESS_TOKEN, accessToken, jwtTokenProvider.getExpiration(), isDev));
      response.addCookie(createCookie(REFRESH_TOKEN, refreshToken, refreshTokenService.getRefreshTokenDurationMs(), isDev));

      return userMapper.toUserResponseDto(newUSer);
  }

  @Override
  public UserResponseDto logIn(
     UserLoginDto loginDto,
     @NonNull  HttpServletResponse response){

      final var principal = (CustomUserPrincipal) authenticationManager
          .authenticate(new UsernamePasswordAuthenticationToken(loginDto.email(),
            loginDto.password()
          )).getPrincipal();

      final var accessToken = jwtTokenProvider.generateToken(principal.user());
      final var refreshToken = refreshTokenService.createRefreshToken(principal.user());
      final var  isDev = environment.equalsIgnoreCase("dev");

      response.addCookie(createCookie(ACCESS_TOKEN, accessToken, jwtTokenProvider.getExpiration(), isDev));
      response.addCookie(createCookie(REFRESH_TOKEN, refreshToken, refreshTokenService.getRefreshTokenDurationMs(), isDev));

    return userMapper.toUserResponseDto(principal.user());
  }

  @Override
  public CustomUserPrincipal singIn(UserLoginDto loginDto, HttpServletResponse response) {

    final var principal = (CustomUserPrincipal) authenticationManager
       .authenticate(new UsernamePasswordAuthenticationToken(loginDto.email(),
          loginDto.password()
       )).getPrincipal();

    final var accessToken = jwtTokenProvider.generateToken(principal.user());
    final var refreshToken = refreshTokenService.createRefreshToken(principal.user());
    final var  isDev = environment.equalsIgnoreCase("dev");

    response.addCookie(createCookie(ACCESS_TOKEN, accessToken, jwtTokenProvider.getExpiration(), isDev));
    response.addCookie(createCookie(REFRESH_TOKEN, refreshToken, refreshTokenService.getRefreshTokenDurationMs(), isDev));
    return  principal;

  }

  @Override
  public StatusResponseDto isUserExists(String email){
    var responseStatus = new StatusResponseDto();

    if(userRepository.existsByEmail(email)) {
      responseStatus.setMessage("Account Updated Success!");
      responseStatus.setStatus(HttpStatus.OK);
      responseStatus.setStatusCode(HttpStatus.OK.value());
    }else {
      responseStatus.setMessage("Account Created Success!");
      responseStatus.setStatus(HttpStatus.CREATED);
      responseStatus.setStatusCode(HttpStatus.CREATED.value());
    }
    return responseStatus;

  }

  private Cookie createCookie(String name, String value, long maxAgeMs, boolean isDev) {
    Cookie cookie = new Cookie(name, value);
    cookie.setHttpOnly(!isDev);
    cookie.setSecure(!isDev);
    cookie.setPath("/");
    cookie.setMaxAge(Math.max(1, (int) (maxAgeMs / 1000)));
    return cookie;
  }

  /* Overriding existing User with the updated info */
  private User updateInfo(User user, UserRequestDto requestDto) {
    user.setName(requestDto.name());
    user.setPassword(requestDto.password());
    return user;
  }

  private Set<Role> getDefaultRoles(String email,User user){
      if (userRepository.existsByEmail(email)){
        return user.getRoles();
      }else {
        return email.equals("sam@gmail.com") ? getAdminAuthorities() : getUserAuthorities();
      }
  }

  private Set<Role> getAdminAuthorities(){
    return Set.of(
       roleRepository.findByName(RoleName.ROLE_ADMIN)
          .orElseThrow(() -> new RuntimeException("ROLE_ADMIN Not Found")),
       roleRepository.findByName(RoleName.ROLE_USER)
          .orElseThrow(() -> new RuntimeException("ROLE_USER Not Found")));
  }

  private Set<Role> getUserAuthorities(){
    return Set.of(
       roleRepository.findByName(RoleName.ROLE_ADMIN)
          .orElseThrow(() -> new RuntimeException("ROLE_ADMIN Not Found")),
       roleRepository.findByName(RoleName.ROLE_USER)
          .orElseThrow(() -> new RuntimeException("ROLE_USER Not Found")));
  }

}
