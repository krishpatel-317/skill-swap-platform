package com.skillswap.service;

import com.skillswap.entity.Review;
import com.skillswap.entity.SwapRequest;
import com.skillswap.entity.User;
import com.skillswap.repository.ReviewRepository;
import com.skillswap.repository.SwapRequestRepository;
import com.skillswap.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final SwapRequestRepository swapRequestRepository;
    private final UserRepository userRepository;

    // -------------------------
    // CREATE - Create Review
    // -------------------------
    @Transactional
    public Review createReview(Long swapRequestId,
                               String reviewerUsername,
                               Integer rating,
                               String comment) {

        SwapRequest swapRequest = swapRequestRepository.findById(swapRequestId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Swap request not found with ID: " + swapRequestId));

        // Only ACCEPTED swaps can be reviewed
        if (swapRequest.getStatus() != SwapRequest.SwapStatus.ACCEPTED) {
            throw new IllegalStateException(
                    "Can only review ACCEPTED swap requests");
        }

        // Prevent duplicate reviews for the same swap
        if (reviewRepository.existsBySwapRequestId(swapRequestId)) {
            throw new IllegalStateException(
                    "A review already exists for this swap request");
        }

        User reviewer = userRepository.findByUsername(reviewerUsername)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found: " + reviewerUsername));

        // Only sender or receiver can leave a review
        boolean isSender = swapRequest.getSender().getId().equals(reviewer.getId());
        boolean isReceiver = swapRequest.getReceiver().getId().equals(reviewer.getId());

        if (!isSender && !isReceiver) {
            throw new AccessDeniedException(
                    "Only participants of the swap can leave a review");
        }

        // Reviewee is the other party
        User reviewee = isSender
                ? swapRequest.getReceiver()
                : swapRequest.getSender();

        Review review = Review.builder()
                .swapRequest(swapRequest)
                .reviewer(reviewer)
                .reviewee(reviewee)
                .rating(rating)
                .comment(comment)
                .build();

        return reviewRepository.save(review);
    }

    // -------------------------
    // READ - Get Reviews for User
    // -------------------------
    @Transactional(readOnly = true)
    public List<Review> getReviewsForUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException(
                    "User not found with ID: " + userId);
        }
        return reviewRepository.findByRevieweeId(userId);
    }

    // -------------------------
    // UPDATE - Update Review
    // -------------------------
    @Transactional
    public Review updateReview(Long id, Integer rating, String comment,
                               String requesterUsername) {

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Review not found with ID: " + id));

        // Only the reviewer can update their own review
        if (!review.getReviewer().getUsername().equals(requesterUsername)) {
            throw new AccessDeniedException(
                    "You can only update your own reviews");
        }

        review.setRating(rating);
        review.setComment(comment);

        return reviewRepository.save(review);
    }

    // -------------------------
    // DELETE - Delete Review (ADMIN only)
    // -------------------------
    @Transactional
    public void deleteReview(Long id, String requesterUsername) {
        User requester = userRepository.findByUsername(requesterUsername)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found: " + requesterUsername));

        // Only ADMIN can delete reviews
        if (requester.getRole() != User.Role.ADMIN) {
            throw new AccessDeniedException(
                    "Only admin can delete reviews");
        }

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Review not found with ID: " + id));

        reviewRepository.delete(review);
    }
}