package com.careerpro.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "career_recommendations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CareerRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    @Column(name = "career_title", nullable = false, length = 150)
    private String careerTitle;

    @Column(name = "confidence_score", precision = 5, scale = 2)
    private BigDecimal confidenceScore;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(name = "required_skills", columnDefinition = "TEXT")
    private String requiredSkills;

    @Column(name = "missing_skills", columnDefinition = "TEXT")
    private String missingSkills;

    @Column(name = "salary_range", length = 100)
    private String salaryRange;

    @Column(name = "demand_level", length = 50)
    private String demandLevel;

    @Column(name = "future_scope", columnDefinition = "TEXT")
    private String futureScope;

    @Column(name = "companies_hiring", columnDefinition = "TEXT")
    private String companiesHiring;

    @Column(name = "suggested_projects", columnDefinition = "TEXT")
    private String suggestedProjects;

    @Column(name = "rank_order")
    @Builder.Default
    private Integer rankOrder = 1;

    @CreationTimestamp
    @Column(name = "generated_at", updatable = false)
    private LocalDateTime generatedAt;
}
