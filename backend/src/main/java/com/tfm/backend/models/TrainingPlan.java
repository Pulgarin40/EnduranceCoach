package com.tfm.backend.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "training_plans")
public class TrainingPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User athlete;

    @Column(nullable = false)
    private String goal;

    @Column(nullable = false)
    private String currentFitnessLevel;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String generatedPlan;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
