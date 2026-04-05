package com.skillswap.service;

import com.skillswap.entity.Skill;
import com.skillswap.entity.SwapRequest;
import com.skillswap.entity.User;
import com.skillswap.repository.SkillRepository;
import com.skillswap.repository.SwapRequestRepository;
import com.skillswap.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SwapRequestService {

    private final SwapRequestRepository swapRequestRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;

    // -------------------------
    // CREATE SWAP REQUEST
    // -------------------------
    @Transactional
    public SwapRequest createSwapRequest(Long senderId,
                                         Long receiverId,
                                         Long offeredSkillId,
                                         Long requestedSkillId,
                                         String message) {

        User sender = getUserById(senderId);
        User receiver = getUserById(receiverId);

        if (sender.getId().equals(receiver.getId())) {
            throw new IllegalArgumentException(
                    "Sender and receiver cannot be the same user");
        }

        Skill offeredSkill = getSkillById(offeredSkillId);
        Skill requestedSkill = getSkillById(requestedSkillId);

        // Validate that offered skill belongs to sender
        if (!offeredSkill.getOwner().getId().equals(senderId)) {
            throw new IllegalArgumentException(
                    "Offered skill does not belong to sender");
        }

        // Validate that requested skill belongs to receiver
        if (!requestedSkill.getOwner().getId().equals(receiverId)) {
            throw new IllegalArgumentException(
                    "Requested skill does not belong to receiver");
        }

        // 🔥 PREVENT DUPLICATE PENDING REQUESTS
        if (swapRequestRepository
                .existsBySenderIdAndReceiverIdAndOfferedSkillIdAndRequestedSkillIdAndStatus(
                        senderId,
                        receiverId,
                        offeredSkillId,
                        requestedSkillId,
                        SwapRequest.SwapStatus.PENDING)) {

            throw new IllegalStateException(
                    "Duplicate pending swap request already exists");
        }

        SwapRequest request = SwapRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .offeredSkill(offeredSkill)
                .requestedSkill(requestedSkill)
                .message(message)
                .status(SwapRequest.SwapStatus.PENDING)
                .build();

        return swapRequestRepository.save(request);
    }

    // -------------------------
    // ACCEPT REQUEST
    // -------------------------
    @Transactional
    public SwapRequest acceptRequest(Long requestId, String requesterUsername) {
        SwapRequest request = getPendingRequestAndValidateReceiver(
                requestId, requesterUsername);

        request.setStatus(SwapRequest.SwapStatus.ACCEPTED);
        request.setUpdatedAt(LocalDateTime.now());

        return swapRequestRepository.save(request);
    }

    // -------------------------
    // REJECT REQUEST
    // -------------------------
    @Transactional
    public SwapRequest rejectRequest(Long requestId, String requesterUsername) {
        SwapRequest request = getPendingRequestAndValidateReceiver(
                requestId, requesterUsername);

        request.setStatus(SwapRequest.SwapStatus.REJECTED);
        request.setUpdatedAt(LocalDateTime.now());

        return swapRequestRepository.save(request);
    }

    // -------------------------
    // GET BY ID
    // -------------------------
    @Transactional(readOnly = true)
    public SwapRequest getSwapRequestById(Long requestId) {
        return swapRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Swap request not found with ID: " + requestId));
    }

    // -------------------------
    // PRIVATE HELPERS
    // -------------------------
    private SwapRequest getPendingRequestAndValidateReceiver(
            Long requestId, String requesterUsername) {

        SwapRequest request = swapRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Swap request not found with ID: " + requestId));

        if (request.getStatus() != SwapRequest.SwapStatus.PENDING) {
            throw new IllegalStateException(
                    "Swap request is not in PENDING state");
        }

        if (!request.getReceiver().getUsername().equals(requesterUsername)) {
            throw new AccessDeniedException(
                    "Only the receiver can accept/reject this request");
        }

        return request;
    }

    private User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found with ID: " + id));
    }

    private Skill getSkillById(Long id) {
        return skillRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Skill not found with ID: " + id));
    }
}