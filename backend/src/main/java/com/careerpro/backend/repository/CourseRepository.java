package com.careerpro.backend.repository;

import com.careerpro.backend.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByIsActiveTrueOrderByRatingDesc();
    List<Course> findByCategoryAndIsActiveTrue(String category);
    List<Course> findBySkillTagContainingIgnoreCaseAndIsActiveTrue(String skillTag);
    List<Course> findByTypeAndIsActiveTrue(String type);
}
