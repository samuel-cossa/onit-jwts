package com.onit.authentication.jwts.modules.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.onit.authentication.jwts.common.dto.ApiResponse;
import com.onit.authentication.jwts.common.exception.ResourceNotFoundException;
import com.onit.authentication.jwts.modules.user.dto.UserRequestDto;
import com.onit.authentication.jwts.modules.user.service.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 *  UserController
 *
 * @author Samuel Cossa <a href="https://github.com/samuel-cossa">...</a>
 * @version 1.0
 * @email ar.sam.cossa@gmail.com.com
 * @license MIT
 * @since 10/20/25
 */
@Slf4j
@Tag(name = "Users", description = "Endpoints para listar e editar users")
@RequiredArgsConstructor
@RequestMapping("/users")
@RestController
public class UserController {
    final UserService userService;
    private static final String INTERNAL_SERVER_ERROR = "Unexpected error occurred:: ";

    @PostMapping
    public ResponseEntity<ApiResponse> createUser(@RequestBody @Valid UserRequestDto requestDto) {
        try {
            var newUser = userService.createUser(requestDto);
            return ResponseEntity.ok(new ApiResponse(true, "User created successfully!", newUser));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PutMapping
    public ResponseEntity<ApiResponse> updateUser(@RequestBody @Valid UserRequestDto requestDto) {
        try {
            var updatedUser = userService.updateUser(requestDto);
            return ResponseEntity.ok(new ApiResponse(true, "User created successfully!", updatedUser));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse(false, INTERNAL_SERVER_ERROR + e.getMessage(), null));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse> findAll() {
        try {
            var userList = userService.findAll();
            if (!userList.isEmpty()) {
                return ResponseEntity.ok(new ApiResponse(true, "User List has been returned successfully", userList));
            } else {
                return ResponseEntity.ok(new ApiResponse(true, "User List is empty", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new ApiResponse(false, "The User List is Empty! :: " + e.getMessage(), null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> findById(@PathVariable Long id) {
        try {
            var user = userService.findById(id);
            return ResponseEntity.ok(new ApiResponse(true, "User successfully found by ID", user));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

}
