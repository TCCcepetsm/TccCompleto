package com.gravacao.config;

import com.gravacao.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

	private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);
	private static final String BEARER_PREFIX = "Bearer ";
	private static final String AUTH_HEADER = "Authorization";

	private final JwtService jwtService;
	private final CustomUserDetailsService userDetailsService;

	public JwtAuthFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
		this.jwtService = jwtService;
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request,
			@NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain)
			throws ServletException, IOException {

		final String authHeader = request.getHeader(AUTH_HEADER);

		if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
			filterChain.doFilter(request, response);
			return;
		}

		try {
			final String jwt = authHeader.substring(BEARER_PREFIX.length());
			final String userEmail = jwtService.extractUsername(jwt);

			if (userEmail == null) {
				sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Token inválido");
				return;
			}

			if (SecurityContextHolder.getContext().getAuthentication() == null) {
				UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

				if (jwtService.isTokenValid(jwt, userDetails)) {
					String role = jwtService.extractRole(jwt);

					if (role == null) {
						sendError(response, HttpServletResponse.SC_FORBIDDEN, "Token sem permissões definidas");
						return;
					}

					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
							userDetails,
							null,
							Collections.singleton(new SimpleGrantedAuthority(role)));

					authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authToken);
					logger.debug("Usuário autenticado: {} com role: {}", userEmail, role);
				}
			}

			filterChain.doFilter(request, response);

		} catch (Exception e) {
			logger.error("Falha na autenticação JWT", e);
			sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Falha na autenticação: " + e.getMessage());
		}
	}

	private void sendError(HttpServletResponse response, int status, String message) throws IOException {
		logger.error("Erro de autenticação: {}", message);
		response.sendError(status, message);
	}
}