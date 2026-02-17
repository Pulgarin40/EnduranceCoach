package com.tfm.backend.controllers;

import com.tfm.backend.models.dto.RegisterRequest;
import com.tfm.backend.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public org.springframework.http.ResponseEntity<com.tfm.backend.models.dto.AuthResponse> login(
            @RequestBody com.tfm.backend.models.dto.LoginRequest request) {
        return org.springframework.http.ResponseEntity.ok(authService.login(request));
    }
}
