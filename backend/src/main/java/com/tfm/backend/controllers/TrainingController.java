package com.tfm.backend.controllers;

import com.tfm.backend.models.TrainingPlan;
import com.tfm.backend.services.TrainingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

@RestController
@RequestMapping("/api/training")
@RequiredArgsConstructor
public class TrainingController {

    private final TrainingService trainingService;
    private final com.tfm.backend.services.AiTrainingService aiTrainingService;
    private final com.tfm.backend.repositories.TrainingPlanRepository trainingPlanRepository;

    @PostMapping("/generate")
    public ResponseEntity<String> generatePlan(@RequestBody com.tfm.backend.dto.TrainingRequest request,
            org.springframework.security.core.Authentication authentication) {
        String generatedPlanJson = aiTrainingService.generateTrainingPlan(request);
        return ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .body(generatedPlanJson);
    }

    @PostMapping("/save")
    public ResponseEntity<TrainingPlan> savePlan(@RequestBody com.tfm.backend.models.dto.TrainingPlanRequest request,
            org.springframework.security.core.Authentication authentication) {
        String userEmail = authentication.getName();
        TrainingPlan savedPlan = trainingService.savePlan(userEmail, request);
        return ResponseEntity.ok(savedPlan);
    }

    @org.springframework.web.bind.annotation.GetMapping("/history")
    public ResponseEntity<java.util.List<TrainingPlan>> getTrainingHistory(Principal principal) {
        String userEmail = principal.getName();
        java.util.List<TrainingPlan> history = trainingService.getTrainingHistory(userEmail);
        return ResponseEntity.ok(history);
    }

    @Transactional(readOnly = true)
    @org.springframework.web.bind.annotation.GetMapping("/my-plans")
    public ResponseEntity<java.util.List<TrainingPlan>> getMyPlans(Principal principal) {
        String email = principal.getName();
        java.util.List<TrainingPlan> plans = trainingPlanRepository.findByAthleteEmailOrderByCreatedAtDesc(email);
        return ResponseEntity.ok(plans);
    }
}
