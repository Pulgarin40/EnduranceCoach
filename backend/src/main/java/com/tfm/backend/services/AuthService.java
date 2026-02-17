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
}
