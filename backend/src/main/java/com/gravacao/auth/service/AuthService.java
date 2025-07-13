package com.gravacao.auth.service;

import com.gravacao.auth.dto.AuthResponseDTO;
import com.gravacao.auth.dto.LoginRequestDTO;
import com.gravacao.auth.dto.RegisterRequestDTO;
import com.gravacao.auth.model.Role;
import com.gravacao.auth.model.User;
import com.gravacao.auth.repository.RoleRepository;
import com.gravacao.auth.repository.UserRepository;
import com.gravacao.auth.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthResponseDTO register(RegisterRequestDTO registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email já registrado!");
        }

        User user = new User();
        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Role 'ROLE_USER' não encontrada!"));
        user.setRoles(Collections.singleton(userRole));

        User savedUser = userRepository.save(user);

        String token = jwtUtil.generateToken(savedUser.getEmail());
        List<String> roles = savedUser.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        return new AuthResponseDTO(token, savedUser.getEmail(), savedUser.getName(), roles);
    }

    public AuthResponseDTO authenticate(LoginRequestDTO loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));

        String token = jwtUtil.generateToken(user.getEmail());
        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        return new AuthResponseDTO(token, user.getEmail(), user.getName(), roles);
    }

    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }
}

