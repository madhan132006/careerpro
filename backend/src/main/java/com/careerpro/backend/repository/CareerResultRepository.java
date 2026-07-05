package com.careerpro.backend.repository;

import com.careerpro.backend.entity.CareerResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CareerResultRepository extends JpaRepository<CareerResult, Long> {
    Optional<CareerResult> findTopByUserIdOrderByTestDateDesc(Long userId);
}
