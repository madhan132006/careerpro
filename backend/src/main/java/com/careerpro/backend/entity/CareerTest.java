package com.careerpro.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "career_tests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CareerTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;

    @Column(nullable = false, length = 50)
    private String category;   // TECHNICAL, CREATIVE, BUSINESS, RESEARCH, MANAGEMENT

    @Column(name = "option_a", length = 255)
    private String optionA;

    @Column(name = "option_b", length = 255)
    private String optionB;

    @Column(name = "option_c", length = 255)
    private String optionC;

    @Column(name = "option_d", length = 255)
    private String optionD;

    @Column(name = "weight_a")
    @Builder.Default
    private Integer weightA = 1;

    @Column(name = "weight_b")
    @Builder.Default
    private Integer weightB = 2;

    @Column(name = "weight_c")
    @Builder.Default
    private Integer weightC = 3;

    @Column(name = "weight_d")
    @Builder.Default
    private Integer weightD = 4;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
