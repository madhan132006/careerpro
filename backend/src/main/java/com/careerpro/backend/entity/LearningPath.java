package com.careerpro.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "learning_paths")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningPath {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    @Column(name = "career_title", length = 150)
    private String careerTitle;

    @Column(name = "roadmap_json", columnDefinition = "LONGTEXT")
    private String roadmapJson;

    @Column(name = "total_weeks")
    @Builder.Default
    private Integer totalWeeks = 12;

    @Column(name = "current_week")
    @Builder.Default
    private Integer currentWeek = 1;

    @Column(name = "completion_percent", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal completionPercent = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(name = "generated_at", updatable = false)
    private LocalDateTime generatedAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
