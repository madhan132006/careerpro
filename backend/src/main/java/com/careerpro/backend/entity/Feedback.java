package com.careerpro.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "feedback")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User user;

    @Column(length = 150)
    private String name;

    @Column(length = 150)
    private String email;

    @Column(length = 200)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column
    @Builder.Default
    private Integer rating = 5;

    @Column(length = 20)
    @Builder.Default
    private String status = "OPEN";   // OPEN, IN_PROGRESS, RESOLVED

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
