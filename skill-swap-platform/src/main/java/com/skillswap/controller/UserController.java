package com.skillswap.controller;

import com.skillswap.dto.UserDTO;
import com.skillswap.entity.User;
import com.skillswap.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // CREATE - Register User (Public)
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

    // READ - Get User by ID
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO.Response> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(UserDTO.Response.fromEntity(user));
    }

    // UPDATE - Update User
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO.Response> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserDTO.RegisterRequest request) {

        User updatedUser = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .bio(request.getBio())
                .build();

        User updated = userService.updateUser(id, updatedUser);
        return ResponseEntity.ok(UserDTO.Response.fromEntity(updated));
    }

    // DELETE - Delete User (ADMIN only)
//    @DeleteMapping("/{id}")
//    public ResponseEntity<String> deleteUser(
//            @PathVariable Long id,
//            @AuthenticationPrincipal UserDetails userDetails) {
//        userService.deleteUser(id, userDetails.getUsername());
//        return ResponseEntity.ok("User deleted successfully");
//    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        userService.deleteUser(id, userDetails.getUsername());

        return ResponseEntity.noContent().build();
    }
}