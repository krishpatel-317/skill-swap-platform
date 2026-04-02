package com.skillswap.controller;

import com.skillswap.dto.SkillDTO;
import com.skillswap.entity.Skill;
import com.skillswap.service.SkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    /**
     * POST /skills
     * Create a new skill (authenticated).
     */
    @PostMapping
    public ResponseEntity<SkillDTO.Response> createSkill(
            @Valid @RequestBody SkillDTO.Request request) {

        Skill skill = Skill.builder()
                .name(request.getName())
                .description(request.getDescription())
                .level(request.getLevel())
                .build();

        Skill saved = skillService.createSkill(skill, request.getOwnerId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SkillDTO.Response.fromEntity(saved));
    }

    /**
     * GET /skills
     * Get all skills (authenticated).
     */
    @GetMapping
    public ResponseEntity<List<SkillDTO.Response>> getAllSkills() {
        List<SkillDTO.Response> skills = skillService.getAllSkills()
                .stream()
                .map(SkillDTO.Response::fromEntity)
                .toList();
        return ResponseEntity.ok(skills);
    }

    /**
     * DELETE /skills/{id}
     * Delete a skill (ADMIN only, enforced by SecurityConfig).
     * Additional owner check is done inside SkillService.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSkill(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        skillService.deleteSkill(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}