package com.tfm.backend.services;

import com.tfm.backend.models.TrainingPlan;
import com.tfm.backend.models.User;
import com.tfm.backend.models.dto.TrainingPlanRequest;
import com.tfm.backend.repositories.TrainingPlanRepository;
import com.tfm.backend.repositories.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrainingService {

        private final TrainingPlanRepository trainingPlanRepository;
        private final UserRepository userRepository;
        private final ChatModel chatModel;

        public TrainingPlan generatePlan(String userEmail, TrainingPlanRequest request) {
                User user = userRepository.findByEmail(userEmail)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                String prompt = String.format(
                                "Actúa como un entrenador personal de élite. Crea un plan de entrenamiento detallado para un atleta cuyo objetivo es: %s, y su nivel de forma actual es: %s. Devuelve solo el plan en formato Markdown.",
                                request.getGoal(),
                                request.getCurrentFitnessLevel());

                String generatedPlanContent = chatModel.call(prompt);

                TrainingPlan trainingPlan = TrainingPlan.builder()
                                .athlete(user)
                                .goal(request.getGoal())
                                .currentFitnessLevel(request.getCurrentFitnessLevel())
                                .generatedPlan(generatedPlanContent)
                                .build();

                return trainingPlanRepository.save(trainingPlan);
        }

        public List<TrainingPlan> getTrainingHistory(String userEmail) {
                User user = userRepository.findByEmail(userEmail)
                                .orElseThrow(() -> new RuntimeException("User not found"));
                return trainingPlanRepository.findByAthleteId(user.getId());
        }
}
