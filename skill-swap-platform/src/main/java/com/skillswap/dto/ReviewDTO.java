package com.skillswap.dto;

import com.skillswap.entity.Review;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class ReviewDTO {

    // -------------------------
    // Request DTO
    // -------------------------
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {

        @NotNull(message = "Swap request ID is required")
        private Long swapRequestId;

        @NotNull(message = "Rating is required")
        @Min(value = 1, message = "Rating must be at least 1")
        @Max(value = 5, message = "Rating must not exceed 5")
        private Integer rating;

        @Size(max = 1000, message = "Comment cannot exceed 1000 characters")
        private String comment;
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
        private Long swapRequestId;
        private Long reviewerId;
        private String reviewerUsername;
        private Long revieweeId;
        private String revieweeUsername;
        private Integer rating;
        private String comment;
        private LocalDateTime createdAt;

        public static Response fromEntity(Review review) {
            return Response.builder()
                    .id(review.getId())
                    .swapRequestId(review.getSwapRequest().getId())
                    .reviewerId(review.getReviewer().getId())
                    .reviewerUsername(review.getReviewer().getUsername())
                    .revieweeId(review.getReviewee().getId())
                    .revieweeUsername(review.getReviewee().getUsername())
                    .rating(review.getRating())
                    .comment(review.getComment())
                    .createdAt(review.getCreatedAt())
                    .build();
        }
    }
}