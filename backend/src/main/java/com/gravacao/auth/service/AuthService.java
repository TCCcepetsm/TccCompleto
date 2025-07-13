package com.gravacao.auth.service;

import com.gravacao.auth.dto.AuthResponseDTO;
import com.gravacao.auth.dto.LoginRequestDTO;
import com.gravacao.auth.dto.RegisterRequestDTO;
import com.gravacao.controller.entity.enuns.Roles;
import com.gravacao.controller.entity.Usuario;
import com.gravacao.repository.UsuarioRepository;
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
    private UsuarioRepository usuarioRepository;

    // @Autowired
    // private RoleRepository roleRepository; // REMOVIDO: Não é mais necessário

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthResponseDTO register(RegisterRequestDTO registerRequest) {
        if (usuarioRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email já registrado!");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(registerRequest.getName()); // Usando setName da DTO, que mapeia para nome no Usuario
        usuario.setEmail(registerRequest.getEmail());
        usuario.setSenha(passwordEncoder.encode(registerRequest.getPassword()));

        // --- MUDANÇA AQUI: Definindo roles usando o enum Roles ---
        // Você não busca mais uma Role no banco, você adiciona o enum diretamente.
        // Se você quiser que todo usuário registrado tenha a role "ROLE_USUARIO" por padrão:
        usuario.setRoles(Collections.singleton(Roles.ROLE_USUARIO)); // Adiciona a role padrão

        // Se o seu enum Roles tiver um método estático para encontrar por nome, você poderia usar:
        // Roles usuarioRoleEnum = Roles.fromName("ROLE_USUARIO")
        //     .orElseThrow(() -> new RuntimeException("Role 'ROLE_USUARIO' não encontrada no enum!"));
        // usuario.setRoles(Collections.singleton(usuarioRoleEnum));


        Usuario savedUsuario = usuarioRepository.save(usuario);

        String token = jwtUtil.generateToken(savedUsuario.getEmail());
        // --- MUDANÇA AQUI: Mapeando roles do enum para String ---
        List<String> roles = savedUsuario.getRoles().stream()
                .map(Roles::getAuthority) // Usa getAuthority() do seu enum Roles
                .collect(Collectors.toList());

        return new AuthResponseDTO(token, savedUsuario.getEmail(), savedUsuario.getNome(), roles);
    }

    public AuthResponseDTO authenticate(LoginRequestDTO loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Usuario usuario = usuarioRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));

        String token = jwtUtil.generateToken(usuario.getEmail());
        // --- MUDANÇA AQUI: Mapeando roles do enum para String ---
        List<String> roles = usuario.getRoles().stream()
                .map(Roles::getAuthority) // Usa getAuthority() do seu enum Roles
                .collect(Collectors.toList());

        // Note: registerRequest.getName() era usado na criação do usuário, mas aqui você deve usar o nome do usuário recuperado do banco.
        return new AuthResponseDTO(token, usuario.getEmail(), usuario.getNome(), roles);
    }

    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }
}