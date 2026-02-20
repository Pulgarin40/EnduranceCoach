package com.tfm.backend.controllers;

import com.tfm.backend.models.AthleteMetrics;
import com.tfm.backend.services.MetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/metrics")
@RequiredArgsConstructor
public class MetricsController {

    private final MetricsService metricsService;

    @PostMapping
    public ResponseEntity<AthleteMetrics> saveOrUpdateMetrics(@RequestBody AthleteMetrics metrics,
            Principal principal) {
        AthleteMetrics savedMetrics = metricsService.saveOrUpdateMetrics(principal.getName(), metrics);
        return ResponseEntity.ok(savedMetrics);
    }

    @GetMapping
    public ResponseEntity<AthleteMetrics> getMetrics(Principal principal) {
        try {
            AthleteMetrics metrics = metricsService.getMetrics(principal.getName());
            return ResponseEntity.ok(metrics);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
