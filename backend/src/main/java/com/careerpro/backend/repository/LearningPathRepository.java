package com.careerpro.backend.repository;

import com.careerpro.backend.entity.LearningPath;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LearningPathRepository extends JpaRepository<LearningPath, Long> {
    Optional<LearningPath> findTopByUserIdOrderByGeneratedAtDesc(Long userId);
}
