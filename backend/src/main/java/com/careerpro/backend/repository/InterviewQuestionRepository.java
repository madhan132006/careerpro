package com.careerpro.backend.repository;

import com.careerpro.backend.entity.InterviewQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewQuestionRepository extends JpaRepository<InterviewQuestion, Long> {
    List<InterviewQuestion> findByUserIdOrderByGeneratedAtDesc(Long userId);
    List<InterviewQuestion> findByUserIdAndQuestionType(Long userId, String questionType);
    void deleteByUserId(Long userId);
}
