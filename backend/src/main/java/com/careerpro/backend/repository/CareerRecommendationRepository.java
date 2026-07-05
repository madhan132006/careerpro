package com.careerpro.backend.repository;

import com.careerpro.backend.entity.CareerRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CareerRecommendationRepository extends JpaRepository<CareerRecommendation, Long> {
    List<CareerRecommendation> findByUserIdOrderByRankOrderAsc(Long userId);
    void deleteByUserId(Long userId);
}
