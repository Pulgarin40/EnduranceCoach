package com.tfm.backend.services;

import com.tfm.backend.dto.NutritionRequest;
import com.tfm.backend.models.AthleteMetrics;
import com.tfm.backend.models.NutritionPlan;
import com.tfm.backend.models.User;
import com.tfm.backend.repositories.NutritionPlanRepository;
import com.tfm.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NutritionService {

        private static final Logger logger = LoggerFactory.getLogger(NutritionService.class);

        private final ChatModel chatModel;
        private final UserRepository userRepository;
        private final NutritionPlanRepository nutritionPlanRepository;
        private final MetricsService metricsService;

        public NutritionPlan generateNutritionPlan(String userEmail, NutritionRequest request) {
                User user = userRepository.findByEmail(userEmail)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                AthleteMetrics metrics = null;
                try {
                        metrics = metricsService.getMetrics(userEmail);
                } catch (Exception e) {
                        logger.warn("No se pudieron obtener métricas para el usuario {}.", userEmail);
                }

                Double weight = metrics != null && metrics.getWeight() != null ? metrics.getWeight()
                                : request.getWeight();
                if (weight == null) {
                        weight = 70.0; // Ultimate fallback
                        logger.warn("Peso no disponible, se usará el valor por defecto de {} kg para la generación del plan nutricional.",
                                        weight);
                }

                String prompt = String.format(
                                "Eres un nutricionista deportivo de élite. Calcula la estrategia para un atleta de %.1f kg que va a competir en %s. El atleta pesa %.1f kg. Usa este peso exacto para calcular los gramos de carbohidratos por hora.\n"
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
                                weight, request.getGoal(), weight);

                String generatedPlan = chatModel.call(prompt);

                NutritionPlan plan = NutritionPlan.builder()
                                .athlete(user)
                                .weight(weight)
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
