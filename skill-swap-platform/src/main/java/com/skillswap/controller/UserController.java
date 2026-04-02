package com.skillswap.controller;

import com.skillswap.dto.UserDTO;
import com.skillswap.entity.User;
import com.skillswap.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * POST /users/register
     * Register a new user (public endpoint).
     */
    @PostMapping("/register")
    public ResponseEntity<UserDTO.Response> register(
            @Valid @RequestBody UserDTO.RegisterRequest request) {

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .bio(request.getBio())
                .role(request.getRole() != null ? request.getRole() : User.Role.USER)
                .build();

        User saved = userService.registerUser(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UserDTO.Response.fromEntity(saved));
    }

    /**
     * GET /users/{id}
     * Get a user by ID (authenticated).
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO.Response> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(UserDTO.Response.fromEntity(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        return ResponseEntity.ok(userService.updateUser(id, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}