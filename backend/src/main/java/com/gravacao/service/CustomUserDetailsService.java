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
	public UserDetails loadUserByUsername(String username) { // UsernameNotFoundException é RuntimeException, pode ser
																// omitida ou tratada como CustomAuthenticationException
		Usuario usuario = usuarioRepository.findByEmailWithRoles(username.toLowerCase())
				.orElseThrow(() -> {
					String message = "Credenciais inválidas para: " + username;
					return new CustomAuthenticationException(message, HttpStatus.UNAUTHORIZED);
				});

		if (!usuario.isAtivo()) {
			throw new CustomAuthenticationException(
					"Usuário desativado",
					HttpStatus.FORBIDDEN);
		}

		// CORREÇÃO FINAL AQUI:
		// Se usuario.getRoles() retorna uma Collection<Roles> (o seu enum),
		// basta mapear cada enum para sua string de autoridade e criar o
		// SimpleGrantedAuthority.
		List<GrantedAuthority> authorities = usuario.getRoles().stream()
				// Aqui 'roleEnum' já é um objeto do tipo Roles (o seu enum)
				.map(roleEnum -> new SimpleGrantedAuthority(roleEnum.getAuthority()))
				.collect(Collectors.toList());

		return User.builder()
				.username(usuario.getEmail())
				.password(usuario.getSenha())
				.authorities(authorities)
				.accountLocked(!usuario.isAtivo()) // Se o usuário não está ativo, a conta está bloqueada
				.build();
	}
}