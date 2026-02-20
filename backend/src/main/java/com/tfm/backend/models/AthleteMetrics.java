package com.tfm.backend.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "athlete_metrics")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AthleteMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "athlete_id")
    private User athlete;

    private Double weight;

    private Double height;

    private Integer restingHeartRate;

    private Double vo2Max;

    private Integer ftp;
}
