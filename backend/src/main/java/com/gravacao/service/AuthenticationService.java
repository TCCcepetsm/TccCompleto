package com.gravacao.service;

import com.gravacao.controller.entity.Usuario;
import com.gravacao.exception.AuthenticationException;
import com.gravacao.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthenticationService {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    private final UsuarioRepository usuarioRepository;

    /**
     * Obtém o usuário autenticado com informações básicas
     * @return Usuario autenticado
     * @throws AuthenticationException se não autenticado ou usuário não encontrado
     */
    public Usuario getAuthenticatedUser() {
        final String email = getAuthenticatedEmail();
        logger.debug("Buscando usuário autenticado: {}", email);
        
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("Usuário não encontrado no banco: {}", email);
                    return new AuthenticationException(
                            "Credenciais inválidas - usuário não encontrado",
                            HttpStatus.UNAUTHORIZED);
                });
    }

    /**
     * Obtém o usuário autenticado com seus roles carregados
     * @return Usuario com roles
     * @throws AuthenticationException se não autenticado ou usuário não encontrado
     */
    public Usuario getAuthenticatedUserWithRoles() {
        final String email = getAuthenticatedEmail();
        logger.debug("Buscando usuário autenticado com roles: {}", email);
        
        return usuarioRepository.findByEmailWithRoles(email)
                .orElseThrow(() -> {
                    logger.error("Usuário com roles não encontrado: {}", email);
                    return new AuthenticationException(
                            "Credenciais inválidas - usuário não encontrado",
                            HttpStatus.UNAUTHORIZED);
                });
    }

    /**
     * Método privado para extrair email do contexto de segurança
     * @return email do usuário autenticado
     * @throws AuthenticationException se não autenticado
     */
    private String getAuthenticatedEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            logger.warn("Tentativa de acesso sem autenticação válida");
            throw new AuthenticationException(
                    "Acesso não autorizado - autenticação requerida",
                    HttpStatus.UNAUTHORIZED);
        }

        return authentication.getName();
    }
}