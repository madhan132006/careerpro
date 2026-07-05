package com.careerpro.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "resume_analysis")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    @Column(name = "file_name", length = 255)
    private String fileName;

    @Column(name = "extracted_text", columnDefinition = "LONGTEXT")
    private String extractedText;

    @Column(name = "skills_found", columnDefinition = "TEXT")
    private String skillsFound;

    @Column(name = "projects_found", columnDefinition = "TEXT")
    private String projectsFound;

    @Column(name = "achievements_found", columnDefinition = "TEXT")
    private String achievementsFound;

    @Column(name = "ats_score")
    @Builder.Default
    private Integer atsScore = 0;

    @Column(name = "formatting_score")
    @Builder.Default
    private Integer formattingScore = 0;

    @Column(name = "grammar_score")
    @Builder.Default
    private Integer grammarScore = 0;

    @Column(name = "overall_score")
    @Builder.Default
    private Integer overallScore = 0;

    @Column(name = "improvement_tips", columnDefinition = "LONGTEXT")
    private String improvementTips;

    @CreationTimestamp
    @Column(name = "analyzed_at", updatable = false)
    private LocalDateTime analyzedAt;
}
