package com.gravacao.controller.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {

    private static final String ROLE_PREFIX = "ROLE_";
    private final Usuario usuario;

    public CustomUserDetails(Usuario usuario) {
        Objects.requireNonNull(usuario, "Usuário não pode ser nulo");
        this.usuario = usuario;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return usuario.getRoles().stream()
                .map(role -> {
                    String roleName = role.name();
                    return new SimpleGrantedAuthority(
                            roleName.startsWith(ROLE_PREFIX) ? roleName : ROLE_PREFIX + roleName);
                })
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return usuario.getSenha();
    }

    @Override
    public String getUsername() {
        return usuario.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return usuario.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return usuario.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return usuario.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return usuario.isEnabled();
    }

    public Usuario getUsuario() {
        return usuario;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof CustomUserDetails))
            return false;
        CustomUserDetails that = (CustomUserDetails) o;
        return Objects.equals(getUsername(), that.getUsername());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUsername());
    }
}