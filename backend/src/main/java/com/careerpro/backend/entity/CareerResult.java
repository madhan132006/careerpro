package com.careerpro.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "career_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CareerResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    @Column(name = "technical_score")
    @Builder.Default
    private Integer technicalScore = 0;

    @Column(name = "creative_score")
    @Builder.Default
    private Integer creativeScore = 0;

    @Column(name = "business_score")
    @Builder.Default
    private Integer businessScore = 0;

    @Column(name = "research_score")
    @Builder.Default
    private Integer researchScore = 0;

    @Column(name = "management_score")
    @Builder.Default
    private Integer managementScore = 0;

    @Column(name = "total_score")
    @Builder.Default
    private Integer totalScore = 0;

    @Column(name = "dominant_category", length = 50)
    private String dominantCategory;

    @CreationTimestamp
    @Column(name = "test_date", updatable = false)
    private LocalDateTime testDate;
}
