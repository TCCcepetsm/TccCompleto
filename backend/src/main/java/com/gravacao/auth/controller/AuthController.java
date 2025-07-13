package com.gravacao.auth.controller;

import com.gravacao.auth.dto.AuthResponseDTO;
import com.gravacao.auth.dto.LoginRequestDTO;
import com.gravacao.auth.dto.RegisterRequestDTO;
import com.gravacao.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO registerRequest) {
        try {
            AuthResponseDTO response = authService.register(registerRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody LoginRequestDTO loginRequest) {
        try {
            AuthResponseDTO response = authService.authenticate(loginRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token não fornecido ou inválido");
        }
        String token = authorizationHeader.substring(7);
        if (authService.validateToken(token)) {
            return ResponseEntity.ok("Token válido");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido ou expirado");
        }
    }
}

