package com.tfm.backend.controllers;

import com.tfm.backend.models.TrainingPlan;
import com.tfm.backend.models.dto.TrainingPlanRequest;
import com.tfm.backend.services.TrainingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/training")
@RequiredArgsConstructor
public class TrainingController {

    private final TrainingService trainingService;

    @PostMapping("/generate")
    public ResponseEntity<TrainingPlan> generatePlan(@RequestBody TrainingPlanRequest request, Principal principal) {
        String userEmail = principal.getName();
        TrainingPlan plan = trainingService.generatePlan(userEmail, request);
        return ResponseEntity.ok(plan);
    }
}
