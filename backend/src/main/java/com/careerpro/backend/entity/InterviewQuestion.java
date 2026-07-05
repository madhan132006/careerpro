package com.careerpro.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "interview_questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User user;

    @Column(name = "career_title", length = 150)
    private String careerTitle;

    @Column(name = "question_type", length = 50)
    private String questionType;   // TECHNICAL, HR, CODING, BEHAVIORAL, COMPANY_SPECIFIC

    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;

    @Column(name = "model_answer", columnDefinition = "TEXT")
    private String modelAnswer;

    @Column(length = 20)
    @Builder.Default
    private String difficulty = "MEDIUM";

    @CreationTimestamp
    @Column(name = "generated_at", updatable = false)
    private LocalDateTime generatedAt;
}
