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

    // -------------------------
    // CREATE - Add Skill
    // -------------------------
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

    // -------------------------
    // READ - Get All Skills
    // -------------------------
    @GetMapping
    public ResponseEntity<List<SkillDTO.Response>> getAllSkills() {
        List<SkillDTO.Response> skills = skillService.getAllSkills()
                .stream()
                .map(SkillDTO.Response::fromEntity)
                .toList();
        return ResponseEntity.ok(skills);
    }

    // -------------------------
    // UPDATE - Update Skill (Owner or ADMIN)
    // -------------------------
    @PutMapping("/{id}")
    public ResponseEntity<SkillDTO.Response> updateSkill(
            @PathVariable Long id,
            @Valid @RequestBody SkillDTO.Request request,
            @AuthenticationPrincipal UserDetails userDetails) {

        Skill skill = Skill.builder()
                .name(request.getName())
                .description(request.getDescription())
                .level(request.getLevel())
                .build();

        Skill updated = skillService.updateSkill(id, skill, userDetails.getUsername());
        return ResponseEntity.ok(SkillDTO.Response.fromEntity(updated));
    }

    // -------------------------
    // DELETE - Delete Skill (ADMIN or Owner)
    // -------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSkill(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        skillService.deleteSkill(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}