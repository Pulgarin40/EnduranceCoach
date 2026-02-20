package com.tfm.backend.controllers;

import com.tfm.backend.dto.NutritionPlanRequest;
import com.tfm.backend.models.NutritionPlan;
import com.tfm.backend.services.NutritionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/nutrition")
@RequiredArgsConstructor
public class NutritionController {

    private final NutritionService nutritionService;

    @PostMapping("/generate")
    public ResponseEntity<NutritionPlan> generatePlan(@RequestBody NutritionPlanRequest request, Principal principal) {
        NutritionPlan plan = nutritionService.generateNutritionPlan(principal.getName(), request);
        return ResponseEntity.ok(plan);
    }

    @GetMapping("/history")
    public ResponseEntity<List<NutritionPlan>> getHistory(Principal principal) {
        List<NutritionPlan> history = nutritionService.getNutritionHistory(principal.getName());
        return ResponseEntity.ok(history);
    }
}
