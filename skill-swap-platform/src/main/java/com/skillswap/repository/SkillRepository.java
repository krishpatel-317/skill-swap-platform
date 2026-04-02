package com.skillswap.repository;

import com.skillswap.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

    List<Skill> findByOwnerId(Long ownerId);

    List<Skill> findByLevel(Skill.SkillLevel level);

    boolean existsByNameAndOwnerId(String name, Long ownerId);
}