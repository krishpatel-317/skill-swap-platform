package com.skillswap.dto;

import com.skillswap.entity.SwapRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class SwapRequestDTO {

    // -------------------------
    // Request DTO
    // -------------------------
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {

        @NotNull(message = "Sender ID is required")
        private Long senderId;

        @NotNull(message = "Receiver ID is required")
        private Long receiverId;

        @NotNull(message = "Offered skill ID is required")
        private Long offeredSkillId;

        @NotNull(message = "Requested skill ID is required")
        private Long requestedSkillId;

        @NotBlank(message = "Message is required")
        private String message;
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
        private Long senderId;
        private String senderUsername;
        private Long receiverId;
        private String receiverUsername;
        private Long offeredSkillId;
        private String offeredSkillName;
        private Long requestedSkillId;
        private String requestedSkillName;
        private String message;
        private SwapRequest.SwapStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static Response fromEntity(SwapRequest request) {
            return Response.builder()
                    .id(request.getId())
                    .senderId(request.getSender().getId())
                    .senderUsername(request.getSender().getUsername())
                    .receiverId(request.getReceiver().getId())
                    .receiverUsername(request.getReceiver().getUsername())
                    .offeredSkillId(request.getOfferedSkill().getId())
                    .offeredSkillName(request.getOfferedSkill().getName())
                    .requestedSkillId(request.getRequestedSkill().getId())
                    .requestedSkillName(request.getRequestedSkill().getName())
                    .message(request.getMessage())
                    .status(request.getStatus())
                    .createdAt(request.getCreatedAt())
                    .updatedAt(request.getUpdatedAt())
                    .build();
        }
    }
}