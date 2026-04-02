package com.skillswap.dto;

import com.skillswap.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class UserDTO {

    // -------------------------
    // Request DTO (Register)
    // -------------------------
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RegisterRequest {

        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50, message = "Username must be 3-50 characters")
        private String username;

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        private String email;

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        private String password;

        private String bio;

        // Optional: only ADMIN can set this, defaults to USER
        private User.Role role;
    }

    // -------------------------
    // Response DTO
    // -------------------------
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String username;
        private String email;
        private String bio;
        private User.Role role;
        private LocalDateTime createdAt;

        public static Response fromEntity(User user) {
            return Response.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .bio(user.getBio())
                    .role(user.getRole())
                    .createdAt(user.getCreatedAt())
                    .build();
        }
    }
}