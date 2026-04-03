package com.skillswap.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    // -------------------------
    // Enum: Role
    // -------------------------
    public enum Role {
        ADMIN, USER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50)
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Password is required")
    @Column(nullable = false)
    private String password;

    @Column(length = 500)
    private String bio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.USER;

    @Column(name = "created_at", updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    // -------------------------
    // One user can have many skills
    // If user deleted → skills deleted
    // -------------------------
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Skill> skills = new ArrayList<>();

    // -------------------------
    // One user can send many swap requests
    // If user deleted → sent swap requests deleted
    // -------------------------
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SwapRequest> sentRequests = new ArrayList<>();

    // -------------------------
    // One user can receive many swap requests
    // If user deleted → received swap requests deleted
    // -------------------------
    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SwapRequest> receivedRequests = new ArrayList<>();

    // -------------------------
    // Reviews written by this user
    // If user deleted → their reviews deleted
    // -------------------------
    @OneToMany(mappedBy = "reviewer", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Review> reviewsGiven = new ArrayList<>();

    // -------------------------
    // Reviews received by this user
    // If user deleted → reviews about them deleted
    // -------------------------
    @OneToMany(mappedBy = "reviewee", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Review> reviewsReceived = new ArrayList<>();
}