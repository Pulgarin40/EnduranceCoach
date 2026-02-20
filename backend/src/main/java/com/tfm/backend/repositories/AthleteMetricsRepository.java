package com.tfm.backend.repositories;

import com.tfm.backend.models.AthleteMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AthleteMetricsRepository extends JpaRepository<AthleteMetrics, Long> {
    Optional<AthleteMetrics> findByAthleteId(Long athleteId);
}
