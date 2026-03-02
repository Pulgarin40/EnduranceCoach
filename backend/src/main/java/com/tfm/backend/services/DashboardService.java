package com.tfm.backend.services;

import com.tfm.backend.dto.DashboardStatsDTO;
import com.tfm.backend.dto.RecentActivityDTO;
import com.tfm.backend.models.NutritionPlan;
import com.tfm.backend.models.TrainingPlan;
import com.tfm.backend.models.User;
import com.tfm.backend.repositories.NutritionPlanRepository;
import com.tfm.backend.repositories.TrainingPlanRepository;
import com.tfm.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

        private final TrainingPlanRepository trainingPlanRepository;
        private final NutritionPlanRepository nutritionPlanRepository;
        private final UserRepository userRepository;

        public DashboardStatsDTO getDashboardStats() {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication == null || !authentication.isAuthenticated()
                                || "anonymousUser".equals(authentication.getPrincipal())) {
                        throw new RuntimeException(
                                        "Usuario no autenticado. Debe iniciar sesión para ver las estadísticas.");
                }

                String email = authentication.getName();
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException(
                                                "El usuario autenticado no existe en la base de datos."));

                long totalTraining = trainingPlanRepository.countByAthleteId(user.getId());
                long totalNutrition = nutritionPlanRepository.countByAthleteId(user.getId());
                int planesCreados = (int) (totalTraining + totalNutrition);

                List<TrainingPlan> trainingPlans = trainingPlanRepository.findByAthleteId(user.getId());
                List<NutritionPlan> nutritionPlans = nutritionPlanRepository.findByAthleteId(user.getId());

                // Simple streak logic for visual purposes
                int rachaActual = (planesCreados > 0) ? (planesCreados / 2) + 1 : 0;

                List<RecentActivityDTO> activities = new ArrayList<>();

                for (TrainingPlan tp : trainingPlans) {
                        activities.add(RecentActivityDTO.builder()
                                        .id("T-" + tp.getId())
                                        .title(tp.getGoal())
                                        .type("Entrenamiento")
                                        .date(tp.getCreatedAt())
                                        .build());
                }

                for (NutritionPlan np : nutritionPlans) {
                        activities.add(RecentActivityDTO.builder()
                                        .id("N-" + np.getId())
                                        .title(np.getGoal())
                                        .type("Nutrición")
                                        .date(np.getCreatedAt())
                                        .build());
                }

                // Sort by date descending and take top 3
                List<RecentActivityDTO> recentActivities = activities.stream()
                                .sorted(Comparator.comparing(RecentActivityDTO::getDate,
                                                Comparator.nullsLast(Comparator.reverseOrder())))
                                .limit(3)
                                .collect(Collectors.toList());

                return DashboardStatsDTO.builder()
                                .totalPlans(planesCreados)
                                .currentStreak(rachaActual)
                                .recentActivities(recentActivities)
                                .build();
        }
}
