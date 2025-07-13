package com.gravacao.service;

import com.gravacao.controller.entity.Usuario;
import com.gravacao.exception.CustomAuthenticationException;
import com.gravacao.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Usuario usuario = usuarioRepository.findByEmailWithRoles(username.toLowerCase())
                .orElseThrow(() -> new CustomAuthenticationException(
                    "Credenciais inválidas", 
                    HttpStatus.UNAUTHORIZED));

        if (!usuario.isAtivo()) {
            throw new CustomAuthenticationException(
                    "Usuário desativado. Contate o administrador.",
                    HttpStatus.FORBIDDEN);
        }

        List<GrantedAuthority> authorities = usuario.getRoles().stream()
                .map(role -> (GrantedAuthority) role) // Seu enum Roles já implementa GrantedAuthority
                .toList();

        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getSenha())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(!usuario.isAtivo())
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}