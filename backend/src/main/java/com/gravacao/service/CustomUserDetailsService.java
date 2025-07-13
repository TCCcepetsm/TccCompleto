package com.gravacao.service;

import com.gravacao.controller.entity.Usuario;
import com.gravacao.exception.CustomAuthenticationException;
import com.gravacao.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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

		// Convertendo as roles do usuário para GrantedAuthority
		List<GrantedAuthority> authorities = usuario.getRoles().stream()
				.map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
				.collect(Collectors.toList());

		return User.builder()
				.username(usuario.getEmail())
				.password(usuario.getSenha())
				.authorities(authorities) // Adicionando as autoridades aqui
				.accountExpired(false)
				.accountLocked(!usuario.isAtivo())
				.credentialsExpired(false)
				.disabled(false)
				.build();
	}
}