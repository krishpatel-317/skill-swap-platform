package com.skillswap.repository;

import com.skillswap.entity.SwapRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SwapRequestRepository extends JpaRepository<SwapRequest, Long> {

    List<SwapRequest> findBySenderId(Long senderId);

    List<SwapRequest> findByReceiverId(Long receiverId);

    List<SwapRequest> findByStatus(SwapRequest.SwapStatus status);
}