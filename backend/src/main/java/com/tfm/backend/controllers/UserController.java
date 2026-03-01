package com.tfm.backend.controllers;

import com.tfm.backend.models.User;
import com.tfm.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user/me")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getMetrics(Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> response = new HashMap<>();
        response.put("weight", user.getWeight());
        response.put("restingHr", user.getRestingHr());
        response.put("maxHr", user.getMaxHr());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/metrics")
    public ResponseEntity<Map<String, Object>> updateMetrics(@RequestBody Map<String, Object> request,
            Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.containsKey("weight") && request.get("weight") != null) {
            user.setWeight(Double.valueOf(request.get("weight").toString()));
        }
        if (request.containsKey("restingHr") && request.get("restingHr") != null) {
            user.setRestingHr(Integer.valueOf(request.get("restingHr").toString()));
        }
        if (request.containsKey("maxHr") && request.get("maxHr") != null) {
            user.setMaxHr(Integer.valueOf(request.get("maxHr").toString()));
        }

        userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("weight", user.getWeight());
        response.put("restingHr", user.getRestingHr());
        response.put("maxHr", user.getMaxHr());

        return ResponseEntity.ok(response);
    }
}
