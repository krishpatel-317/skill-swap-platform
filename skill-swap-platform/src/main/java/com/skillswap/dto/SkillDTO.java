package com.skillswap.dto;

import com.skillswap.entity.Skill;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class SkillDTO {

    // -------------------------
    // Request DTO
    // -------------------------
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {

        @NotBlank(message = "Skill name is required")
        private String name;

        private String description;

        @NotNull(message = "Skill level is required")
        private Skill.SkillLevel level;

        @NotNull(message = "Owner ID is required")
        private Long ownerId;
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
        private String name;
        private String description;
        private Skill.SkillLevel level;
        private Long ownerId;
        private String ownerUsername;
        private LocalDateTime createdAt;

        public static Response fromEntity(Skill skill) {
            return Response.builder()
                    .id(skill.getId())
                    .name(skill.getName())
                    .description(skill.getDescription())
                    .level(skill.getLevel())
                    .ownerId(skill.getOwner().getId())
                    .ownerUsername(skill.getOwner().getUsername())
                    .createdAt(skill.getCreatedAt())
                    .build();
        }
    }
}