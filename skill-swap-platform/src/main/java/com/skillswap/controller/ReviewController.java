package com.skillswap.controller;

import com.skillswap.dto.ReviewDTO;
import com.skillswap.entity.Review;
import com.skillswap.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * POST /reviews
     * Create a review for a completed (ACCEPTED) swap (authenticated).
     */
    @PostMapping
    public ResponseEntity<ReviewDTO.Response> createReview(
            @Valid @RequestBody ReviewDTO.Request request,
            @AuthenticationPrincipal UserDetails userDetails) {

        Review review = reviewService.createReview(
                request.getSwapRequestId(),
                userDetails.getUsername(),
                request.getRating(),
                request.getComment()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ReviewDTO.Response.fromEntity(review));
    }

    /**
     * GET /reviews/user/{userId}
     * Get all reviews received by a user (authenticated).
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewDTO.Response>> getReviewsForUser(
            @PathVariable Long userId) {

        List<ReviewDTO.Response> reviews = reviewService.getReviewsForUser(userId)
                .stream()
                .map(ReviewDTO.Response::fromEntity)
                .toList();

        return ResponseEntity.ok(reviews);
    }
}