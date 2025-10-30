package com.onit.authentication.jwts.security.controller;

import com.onit.authentication.jwts.common.dto.HttpResponse;
import com.onit.authentication.jwts.modules.user.domain.User;
import com.onit.authentication.jwts.modules.user.dto.UserLoginDto;
import com.onit.authentication.jwts.modules.user.dto.UserRequestDto;
import com.onit.authentication.jwts.modules.user.mapper.UserMapper;
import com.onit.authentication.jwts.modules.user.repository.UserRepository;
import com.onit.authentication.jwts.modules.user.service.UserService;
import com.onit.authentication.jwts.security.model.CustomUserPrincipal;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Map;

/**
 * AuthController
 *
 * @author Samuel Cossa <a href="https://github.com/samuel-cossa">...</a>
 * @version 1.0
 * @email ar.sam.cossa@gmail.com.com
 * @license MIT
 * @since 10/22/25
 */

@Slf4j
@Tag(name = "Auth Controller", description = "Endpoints authenticate edit list users")
@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthController {
  final UserService userService;
  final UserMapper userMapper;
  final UserRepository userRepository;
  @Value("${app.environment:dev}")
  private String environment;


  @PostMapping("/sign-in")
  public ResponseEntity<HttpResponse> signIn(@RequestBody @Valid UserLoginDto loginDto, @NonNull HttpServletResponse response){

    var message = "User authentication success!";

    try {
      var principal = userService.singIn(loginDto,response);
      return ResponseEntity.ok().body(
          HttpResponse.builder()
          .timeStamp(ZonedDateTime.now())
          .message(String.format(message,principal.user().getName()))
          .status(HttpStatus.ACCEPTED)
          .statusCode(HttpStatus.ACCEPTED.value())
          .build());
    } catch (Exception e) {

      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
          HttpResponse.builder()
            .timeStamp(ZonedDateTime.now())
            .data(Map.of("Error:", "Invalid Username or Password!"))
            .message(e.getMessage())
            .status(HttpStatus.FORBIDDEN)
            .statusCode(HttpStatus.FORBIDDEN.value())
            .build());
    }
  }

  @PostMapping("/sign-up")
  public ResponseEntity<HttpResponse> signUp(
     @RequestBody @Valid UserRequestDto requestDto,
     @NonNull HttpServletResponse response){

    var statusResponse = userService.isUserExists(requestDto.email());

    try {

      var newUser = userService.signUp(requestDto,response);
      return ResponseEntity.created(getUri(newUser.id())).body(
          HttpResponse.builder()
            .timeStamp(ZonedDateTime.now())
            .data(Map.of("user", newUser))
             .uri(getUri(newUser.id()))
            .message(String.format(statusResponse.getMessage(), requestDto.name()))
            .status(statusResponse.getStatus())
            .statusCode(statusResponse.getStatusCode())
            .build());
    } catch (Exception e) {

      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
          HttpResponse.builder()
            .timeStamp(ZonedDateTime.now())
            .data(Map.of("Error:", "User Not Created!"))
            .message(e.getCause().getMessage())
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .build());
    }
  }

  @GetMapping("/me")
  public ResponseEntity<HttpResponse> me(){
    var auth = SecurityContextHolder.getContext().getAuthentication();
    var user = new User();

    if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof CustomUserPrincipal principal) {
      user = principal.user();
    }

    if (user.getName() != null){

      return ResponseEntity.ok().body(
         HttpResponse.builder()
            .timeStamp(ZonedDateTime.now())
            .data(Map.of("user", userMapper.toUserResponseDto(user)))
            .message("Authenticated User")
            .status(HttpStatus.OK)
            .statusCode(HttpStatus.OK.value())
            .build());
    }else{
      return ResponseEntity.status(HttpStatus.NETWORK_AUTHENTICATION_REQUIRED).body(
         HttpResponse.builder()
            .timeStamp(ZonedDateTime.now())
            .message("Not Authenticated")
            .status(HttpStatus.NETWORK_AUTHENTICATION_REQUIRED)
            .statusCode(HttpStatus.NETWORK_AUTHENTICATION_REQUIRED.value())
            .build());
    }
  }

  private final URI getUri(Long newUserId) {
    return ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(newUserId)
        .toUri();
  }

}
