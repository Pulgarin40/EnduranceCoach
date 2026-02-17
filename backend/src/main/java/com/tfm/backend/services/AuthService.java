package com.tfm.backend.services;

import com.tfm.backend.models.Role;
import com.tfm.backend.models.User;
import com.tfm.backend.models.dto.RegisterRequest;
import com.tfm.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public String register(RegisterRequest request) {
        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ATHLETE)
                .build();
        userRepository.save(user);
        return "Usuario registrado con éxito";
    }

    public com.tfm.backend.models.dto.AuthResponse login(com.tfm.backend.models.dto.LoginRequest request) {
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        var token = jwtService.generateToken(user);
        return com.tfm.backend.models.dto.AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }
}
