package com.skillswap.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "skills")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Skill {

    // -------------------------
    // Enum: SkillLevel
    // -------------------------
    public enum SkillLevel {
        BEGINNER, INTERMEDIATE, EXPERT
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Skill name is required")
    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    @NotNull(message = "Skill level is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SkillLevel level;

    @Column(name = "created_at", updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    // -------------------------
    // Many skills belong to one user
    // -------------------------
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    // -------------------------
    // Swap requests where this skill was OFFERED
    // If skill deleted → those swap requests deleted
    // -------------------------
    @OneToMany(mappedBy = "offeredSkill", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SwapRequest> offeredInRequests = new ArrayList<>();

    // -------------------------
    // Swap requests where this skill was REQUESTED
    // If skill deleted → those swap requests deleted
    // -------------------------
    @OneToMany(mappedBy = "requestedSkill", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SwapRequest> requestedInRequests = new ArrayList<>();
}