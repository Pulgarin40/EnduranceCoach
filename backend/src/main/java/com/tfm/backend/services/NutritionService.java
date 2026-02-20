package com.tfm.backend.services;

import com.tfm.backend.dto.NutritionPlanRequest;
import com.tfm.backend.models.NutritionPlan;
import com.tfm.backend.models.User;
import com.tfm.backend.repositories.NutritionPlanRepository;
import com.tfm.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NutritionService {

    private final ChatModel chatModel;
    private final UserRepository userRepository;
    private final NutritionPlanRepository nutritionPlanRepository;

    public NutritionPlan generateNutritionPlan(String userEmail, NutritionPlanRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String prompt = String.format(
                "Actúa como un nutricionista experto en deportes de resistencia. " +
                        "Tengo un atleta que va a participar en una prueba de tipo: %s. " +
                        "Su preferencia alimenticia es: %s. " +
                        "Genera un plan detallado de carga de carbohidratos (días previos), " +
                        "estrategias de hidratación, y pauta de consumo de geles/hora durante la carrera. " +
                        "Presenta la respuesta en formato de texto claro a modo de informe.",
                request.getRaceType(), request.getDietPreference());

        String generatedPlan = chatModel.call(prompt);

        NutritionPlan plan = NutritionPlan.builder()
                .athlete(user)
                .raceType(request.getRaceType())
                .targetCalories(0) // Valores aproximados por ahora
                .carbsGrams(0)
                .proteinGrams(0)
                .fatGrams(0)
                .hydrationLiters(0.0)
                .generatedPlan(generatedPlan)
                .build();

        // El createdAt se persistirá automáticamente gracias a @PrePersist en la
        // entidad.

        return nutritionPlanRepository.save(plan);
    }

    public List<NutritionPlan> getNutritionHistory(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return nutritionPlanRepository.findByAthleteId(user.getId());
    }
}
