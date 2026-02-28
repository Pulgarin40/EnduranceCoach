package com.tfm.backend.services;

import com.tfm.backend.dto.NutritionRequest;
import com.tfm.backend.models.NutritionPlan;
import com.tfm.backend.models.User;
import com.tfm.backend.repositories.NutritionPlanRepository;
import com.tfm.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NutritionService {

        private final ChatModel chatModel;
        private final UserRepository userRepository;
        private final NutritionPlanRepository nutritionPlanRepository;

        public NutritionPlan generateNutritionPlan(String userEmail, NutritionRequest request) {
                User user = userRepository.findByEmail(userEmail)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                String prompt = String.format(
                                "Eres un nutricionista deportivo de élite. Calcula la estrategia para un atleta de %.1f kg que va a competir en %s.\n"
                                                +
                                                "REGLAS MATEMÁTICAS ESTRICTAS:\n\n" +
                                                "Carga previa: Multiplica el peso por 8g para dar el total de HC diarios.\n\n"
                                                +
                                                "Desayuno pre-carrera: Multiplica el peso por 2g de HC.\n\n" +
                                                "Carrera: Exige entre 60g y 90g de HC por hora, e indica 500mg de sodio por hora.\n\n"
                                                +
                                                "Devuelve ÚNICAMENTE este JSON, sustituyendo los valores por tus cálculos exactos y detallados. NO anides objetos, usa solo texto plano en los valores:\n"
                                                +
                                                "{\n" +
                                                "\"cargaHidratos\": {\n" +
                                                "\"diasPrevios\": \"...\",\n" +
                                                "\"alimentos\": \"...\",\n" +
                                                "\"cantidad\": \"[Cálculo matemático exacto] g/día\"\n" +
                                                "},\n" +
                                                "\"preCarrera\": {\n" +
                                                "\"alimentos\": \"...\",\n" +
                                                "\"cantidad\": \"[Cálculo matemático exacto] g totales\"\n" +
                                                "},\n" +
                                                "\"estrategiaCarrera\": {\n" +
                                                "\"HCporHora\": \"...\",\n" +
                                                "\"tipoSales\": \"...\",\n" +
                                                "\"pauta\": \"...\"\n" +
                                                "}\n" +
                                                "}",
                                request.getWeight(), request.getGoal());

                String generatedPlan = chatModel.call(prompt);

                NutritionPlan plan = NutritionPlan.builder()
                                .athlete(user)
                                .weight(request.getWeight())
                                .goal(request.getGoal())
                                .strategyData(generatedPlan)
                                .build();

                return nutritionPlanRepository.save(plan);
        }

        public List<NutritionPlan> getNutritionHistory(String userEmail) {
                User user = userRepository.findByEmail(userEmail)
                                .orElseThrow(() -> new RuntimeException("User not found"));
                return nutritionPlanRepository.findByAthleteId(user.getId());
        }

        public void deleteNutritionPlan(Long id, String userEmail) {
                User user = userRepository.findByEmail(userEmail)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                NutritionPlan plan = nutritionPlanRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Plan not found"));

                if (!plan.getAthlete().getId().equals(user.getId())) {
                        throw new RuntimeException("Not authorized to delete this plan");
                }

                nutritionPlanRepository.delete(plan);
        }
}
