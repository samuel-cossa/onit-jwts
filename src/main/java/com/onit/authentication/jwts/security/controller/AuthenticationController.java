package com.onit.authentication.jwts.security.controller;

import com.onit.authentication.jwts.common.dto.HttpResponse;
import com.onit.authentication.jwts.modules.user.domain.Role;
import com.onit.authentication.jwts.modules.user.dto.UserLoginDto;
import com.onit.authentication.jwts.modules.user.dto.UserRequestDto;
import com.onit.authentication.jwts.modules.user.mapper.UserMapper;
import com.onit.authentication.jwts.modules.user.repository.UserRepository;
import com.onit.authentication.jwts.modules.user.service.UserService;
import com.onit.authentication.jwts.security.jwt.JwtTokenProvider;
import com.onit.authentication.jwts.security.model.CustomUserPrincipal;
import com.onit.authentication.jwts.security.refresh.RefreshTokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import java.io.IOException;
import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * AuthenticationController
 *
 * @author Samuel Cossa <a href="https://github.com/samuel-cossa">...</a>
 * @version 1.0
 * @email ar.sam.cossa@gmail.com.com
 * @license MIT
 * @since 10/20/25
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    final AuthenticationManager authenticationManager;
    final UserRepository userRepository;
    final UserService userService;
    final PasswordEncoder passwordEncoder;
    final RefreshTokenService refreshTokenService;
    final JwtTokenProvider jwtTokenProvider;
    final UserMapper userMapper;
    @Value("${app.environment:dev}")
    private String environment;


  @PostMapping("/login")
  public String login(
     @RequestBody UserLoginDto requestDto,
     @NonNull HttpServletResponse response) {
    try {
      Authentication authentication = authenticationManager.authenticate(
         new UsernamePasswordAuthenticationToken(
            requestDto.email(),
            requestDto.password()
         )
                                                                        );
      final var principal = (CustomUserPrincipal) authentication.getPrincipal();

      String accessToken = jwtTokenProvider.generateToken(principal.user());
      String refreshToken = refreshTokenService.createRefreshToken(principal.user());

      boolean isDev = environment.equalsIgnoreCase("dev");
      response.addCookie(createCookie("accessToken", accessToken, jwtTokenProvider.getExpiration(), isDev));
      response.addCookie(
          createCookie("refreshToken", refreshToken, refreshTokenService.getRefreshTokenDurationMs(), isDev));
      response.sendRedirect("/api/users");

      return accessToken;
    } catch (Exception e) {
      e.printStackTrace(); // ou log
      return "Login failed: " + e.getMessage();
    }
  }

  @PostMapping("/sign-in")
    public String signIn(
            @RequestBody UserLoginDto requestDto,
            @NonNull HttpServletResponse response) throws IOException {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestDto.email(),
                        requestDto.password()
                )
        );
        final var principal = (CustomUserPrincipal) authentication.getPrincipal();
        final var accessToken = jwtTokenProvider.generateToken(principal.user());
        final var refreshToken = refreshTokenService.createRefreshToken(principal.user());
        final boolean isDev = environment.equalsIgnoreCase("dev");

        response.addCookie(createCookie("accessToken", accessToken, jwtTokenProvider.getExpiration(), isDev));
        response.addCookie(
            createCookie("refreshToken", refreshToken, refreshTokenService.getRefreshTokenDurationMs(), isDev));
    response.sendRedirect("/api/users");
        return accessToken;
    }

    @PostMapping("/sign-up")
    public String signUp(
            @RequestBody UserRequestDto requestDto,
            @NonNull HttpServletResponse response)throws IOException, ServletException {
        if(userRepository.existsByEmail(requestDto.email())){
            return "User Already Exist";
        }

        final var mapedUser = userMapper.toUserEntity(requestDto);
        mapedUser.setPassword(passwordEncoder.encode(mapedUser.getPassword()));
        final var newUSer = userRepository.save(mapedUser);


        final var accessToken = jwtTokenProvider.generateToken(newUSer);
        final var refreshToken = refreshTokenService.createRefreshToken(newUSer);
        final var isDev = environment.equalsIgnoreCase("dev");

        response.addCookie(createCookie("accessToken", accessToken, jwtTokenProvider.getExpiration(), isDev));
        response.addCookie(
            createCookie("refreshToken", refreshToken, refreshTokenService.getRefreshTokenDurationMs(), isDev));
        response.sendRedirect("/api/users");

      return "User Successfully signed up";
    }
//
//    @PostMapping("/register")
//    public ResponseEntity<HttpResponse> saveUser(@RequestBody @Valid UserRequestDto requestDto) throws InterruptedException {
////    TimeUnit.SECONDS.sleep(4);
//
//    final var userDto = userService.createUser(requestDto);
//
//    return ResponseEntity.created(getUri(userDto.id())).body(
//       HttpResponse.builder()
//          .timeStamp(ZonedDateTime.now())
//          .data(Map.of("user", userDto))
//          .message(String.format("User account created for user %s", requestDto.name()))
//          .status(HttpStatus.CREATED)
//          .statusCode(HttpStatus.CREATED.value())
//          .build());
//  }
//
//    private final URI getUri(Long newUserId) {
//        return ServletUriComponentsBuilder
//            .fromCurrentRequest()
//            .path("/{id}")
//            .buildAndExpand(newUserId)
//            .toUri();
//    }
//
//    @GetMapping
//    public String getPosts(){
//    return "It Works!";
//  }

    private Cookie createCookie(String name, String value, long maxAgeMs, boolean isDev) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(!isDev);
        cookie.setSecure(!isDev);
        cookie.setPath("/");
        cookie.setMaxAge(Math.max(1, (int) (maxAgeMs / 1000)));
        return cookie;
    }

}
