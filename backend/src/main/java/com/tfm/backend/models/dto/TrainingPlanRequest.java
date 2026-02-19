package com.tfm.backend.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainingPlanRequest {
    private String goal;
    private String currentFitnessLevel;
}
