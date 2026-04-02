package com.skillswap.repository;

import com.skillswap.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByRevieweeId(Long revieweeId);

    List<Review> findByReviewerId(Long reviewerId);

    Optional<Review> findBySwapRequestId(Long swapRequestId);

    boolean existsBySwapRequestId(Long swapRequestId);
}