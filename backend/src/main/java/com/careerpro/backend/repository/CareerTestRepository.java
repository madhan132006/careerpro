package com.careerpro.backend.repository;

import com.careerpro.backend.entity.CareerTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CareerTestRepository extends JpaRepository<CareerTest, Long> {
    List<CareerTest> findByIsActiveTrue();
    List<CareerTest> findByCategoryAndIsActiveTrue(String category);
}
