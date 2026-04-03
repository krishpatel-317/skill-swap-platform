package com.skillswap.service;

import com.skillswap.entity.Skill;
import com.skillswap.entity.User;
import com.skillswap.repository.SkillRepository;
import com.skillswap.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;
    private final UserRepository userRepository;

    // -------------------------
    // CREATE - Add Skill
    // -------------------------
    @Transactional
    public Skill createSkill(Skill skill, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found with ID: " + ownerId));

        skill.setOwner(owner);
        return skillRepository.save(skill);
    }

    // -------------------------
    // READ - Get All Skills
    // -------------------------
    @Transactional(readOnly = true)
    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }

    // -------------------------
    // UPDATE - Update Skill
    // -------------------------
    @Transactional
    public Skill updateSkill(Long skillId, Skill updatedSkill, String requesterUsername) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Skill not found with ID: " + skillId));

        User requester = userRepository.findByUsername(requesterUsername)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found: " + requesterUsername));

        // Only owner or ADMIN can update
        boolean isAdmin = requester.getRole() == User.Role.ADMIN;
        boolean isOwner = skill.getOwner().getId().equals(requester.getId());

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException(
                    "You do not have permission to update this skill");
        }

        skill.setName(updatedSkill.getName());
        skill.setDescription(updatedSkill.getDescription());
        skill.setLevel(updatedSkill.getLevel()); // ← was missing in your code!

        return skillRepository.save(skill);
    }

    // -------------------------
    // DELETE - Delete Skill (ADMIN or Owner)
    // -------------------------
    @Transactional
    public void deleteSkill(Long skillId, String requesterUsername) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Skill not found with ID: " + skillId));

        User requester = userRepository.findByUsername(requesterUsername)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found: " + requesterUsername));

        // ADMIN can delete any skill; USER can only delete their own
        boolean isAdmin = requester.getRole() == User.Role.ADMIN;
        boolean isOwner = skill.getOwner().getId().equals(requester.getId());

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException(
                    "You do not have permission to delete this skill");
        }

        skillRepository.delete(skill);
    }
}