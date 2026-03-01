package com.tfm.backend.controllers;

import com.tfm.backend.models.User;
import com.tfm.backend.repositories.NutritionPlanRepository;
import com.tfm.backend.repositories.TrainingPlanRepository;
import com.tfm.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final UserRepository userRepository;
    private final TrainingPlanRepository trainingPlanRepository;
    private final NutritionPlanRepository nutritionPlanRepository;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats(Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        int trainingCount = trainingPlanRepository.findByAthleteId(user.getId()).size();
        int nutritionCount = nutritionPlanRepository.findByAthleteId(user.getId()).size();

        int planesCreados = trainingCount + nutritionCount;

        // Simulating the streak based on planesCreados (for example only. Can be
        // customized).
        int rachaActual = (planesCreados > 0) ? (planesCreados / 2) + 1 : 0;

        Map<String, Object> response = new HashMap<>();
        response.put("planesCreados", planesCreados);
        response.put("rachaActual", rachaActual);

        return ResponseEntity.ok(response);
    }
}
