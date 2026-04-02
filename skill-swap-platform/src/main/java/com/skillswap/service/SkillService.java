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

    @Transactional
    public Skill createSkill(Skill skill, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found with ID: " + ownerId));

        skill.setOwner(owner);
        return skillRepository.save(skill);
    }

    @Transactional(readOnly = true)
    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }

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
    public Skill updateSkill(Long id, Skill updatedSkill) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Skill not found"));

        skill.setName(updatedSkill.getName());
        skill.setDescription(updatedSkill.getDescription());

        return skillRepository.save(skill);
    }
}