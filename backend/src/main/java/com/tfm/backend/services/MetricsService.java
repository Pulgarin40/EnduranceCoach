package com.tfm.backend.services;

import com.tfm.backend.models.AthleteMetrics;
import com.tfm.backend.models.User;
import com.tfm.backend.repositories.AthleteMetricsRepository;
import com.tfm.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MetricsService {

    private final AthleteMetricsRepository metricsRepository;
    private final UserRepository userRepository;

    public AthleteMetrics saveOrUpdateMetrics(String userEmail, AthleteMetrics metricsData) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<AthleteMetrics> existingMetricsOpt = metricsRepository.findByAthleteId(user.getId());

        AthleteMetrics metrics;
        if (existingMetricsOpt.isPresent()) {
            metrics = existingMetricsOpt.get();
            metrics.setWeight(metricsData.getWeight());
            metrics.setHeight(metricsData.getHeight());
            metrics.setRestingHeartRate(metricsData.getRestingHeartRate());
            metrics.setVo2Max(metricsData.getVo2Max());
            metrics.setFtp(metricsData.getFtp());
        } else {
            metrics = metricsData;
            metrics.setAthlete(user);
        }

        return metricsRepository.save(metrics);
    }

    public AthleteMetrics getMetrics(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return metricsRepository.findByAthleteId(user.getId())
                .orElseThrow(() -> new RuntimeException("Metrics not found for the user"));
    }
}
