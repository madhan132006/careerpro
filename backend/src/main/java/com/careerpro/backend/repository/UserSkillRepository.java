package com.careerpro.backend.repository;

import com.careerpro.backend.entity.UserSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSkillRepository extends JpaRepository<UserSkill, Long> {
    List<UserSkill> findByUserId(Long userId);
    List<UserSkill> findByUserIdAndCategory(Long userId, String category);
    Optional<UserSkill> findByUserIdAndSkillName(Long userId, String skillName);
    void deleteByUserIdAndSkillName(Long userId, String skillName);
}
