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

        public TrainingPlan savePlan(String userEmail, TrainingPlanRequest request) {
                User user = userRepository.findByEmail(userEmail)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                TrainingPlan trainingPlan = TrainingPlan.builder()
                                .athlete(user)
                                .goal(request.getGoal())
                                .distance(request.getDistance())
                                .weeks(request.getWeeks())
                                .planData(request.getPlanData())
                                .build();

                return trainingPlanRepository.save(trainingPlan);
        }

        public List<TrainingPlan> getTrainingHistory(String userEmail) {
                User user = userRepository.findByEmail(userEmail)
                                .orElseThrow(() -> new RuntimeException("User not found"));
                return trainingPlanRepository.findByAthleteId(user.getId());
        }

        public void deletePlan(Long planId, String userEmail) {
                TrainingPlan plan = trainingPlanRepository.findById(planId)
                                .orElseThrow(() -> new RuntimeException("Training plan not found"));

                if (!plan.getAthlete().getEmail().equals(userEmail)) {
                        throw new RuntimeException("Access denied: You are not the owner of this plan");
                }

                trainingPlanRepository.delete(plan);
        }
}
