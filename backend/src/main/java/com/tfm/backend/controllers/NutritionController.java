package com.tfm.backend.controllers;

import com.tfm.backend.dto.NutritionRequest;
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
    public ResponseEntity<NutritionPlan> generatePlan(@RequestBody NutritionRequest request, Principal principal) {
        NutritionPlan plan = nutritionService.generateNutritionPlan(principal.getName(), request);
        return ResponseEntity.ok(plan);
    }

    @GetMapping
    public ResponseEntity<List<NutritionPlan>> getHistory(Principal principal) {
        List<NutritionPlan> history = nutritionService.getNutritionHistory(principal.getName());
        return ResponseEntity.ok(history);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable Long id, Principal principal) {
        nutritionService.deleteNutritionPlan(id, principal.getName());
        return ResponseEntity.ok().build();
    }
}
