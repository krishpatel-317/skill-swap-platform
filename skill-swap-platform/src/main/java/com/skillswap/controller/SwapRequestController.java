package com.skillswap.controller;

import com.skillswap.dto.SwapRequestDTO;
import com.skillswap.entity.SwapRequest;
import com.skillswap.service.SwapRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/swap-requests")
@RequiredArgsConstructor
public class SwapRequestController {

    private final SwapRequestService swapRequestService;

    /**
     * POST /swap-requests
     * Create a new swap request (authenticated).
     */
    @PostMapping
    public ResponseEntity<SwapRequestDTO.Response> createSwapRequest(
            @Valid @RequestBody SwapRequestDTO.Request request) {

        SwapRequest swapRequest = swapRequestService.createSwapRequest(
                request.getSenderId(),
                request.getReceiverId(),
                request.getOfferedSkillId(),
                request.getRequestedSkillId(),
                request.getMessage()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SwapRequestDTO.Response.fromEntity(swapRequest));
    }

    /**
     * PUT /swap-requests/{id}/accept
     * Accept a swap request (only the receiver can do this).
     */
    @PutMapping("/{id}/accept")
    public ResponseEntity<SwapRequestDTO.Response> acceptRequest(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        SwapRequest updated = swapRequestService.acceptRequest(
                id, userDetails.getUsername());
        return ResponseEntity.ok(SwapRequestDTO.Response.fromEntity(updated));
    }

    /**
     * PUT /swap-requests/{id}/reject
     * Reject a swap request (only the receiver can do this).
     */
    @PutMapping("/{id}/reject")
    public ResponseEntity<SwapRequestDTO.Response> rejectRequest(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        SwapRequest updated = swapRequestService.rejectRequest(
                id, userDetails.getUsername());
        return ResponseEntity.ok(SwapRequestDTO.Response.fromEntity(updated));
    }

    /**
     * GET /swap-requests/{id}
     * Get a swap request by ID (authenticated).
     */
    @GetMapping("/{id}")
    public ResponseEntity<SwapRequestDTO.Response> getSwapRequest(
            @PathVariable Long id) {

        SwapRequest request = swapRequestService.getSwapRequestById(id);
        return ResponseEntity.ok(SwapRequestDTO.Response.fromEntity(request));
    }
}