package com.gravacao.controller.entity; // Ou mantenha com.gravacao.controller.entity; se for o mais adequado para seu projeto

import com.gravacao.controller.entity.enuns.Roles;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder // Permite a criação de instâncias de forma fluida (Usuario.builder()...build())
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuarios", uniqueConstraints = { // Mantém o nome da tabela "usuarios"
        @UniqueConstraint(columnNames = "email"),
        @UniqueConstraint(columnNames = "cpf")
})
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario") // Mantém o nome da coluna id_usuario
    private Long id;

    @Column(name = "ultimo_acesso")
    private LocalDateTime ultimoAcesso;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    @Column(nullable = false) // Garante que o nome não seja nulo no banco
    private String nome; // Renomeado de 'name' para 'nome' para consistência

    @Email(message = "Email deve ser válido")
    @NotBlank(message = "Email é obrigatório")
    @Column(unique = true, nullable = false) // Email deve ser único e não nulo
    private String email;

    @NotBlank(message = "Telefone é obrigatório")
    @Pattern(regexp = "^\\(?\\d{2}\\)?[\\s-]?\\d{4,5}[\\s-]?\\d{4}$", message = "Telefone deve estar no formato (99) 99999-9999")
    private String telefone;

    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "\\d{3}\\.?\\d{3}\\.?\\d{3}\\-?\\d{2}", message = "CPF deve estar no formato 999.999.999-99")
    @Column(unique = true, nullable = false) // CPF deve ser único e não nulo
    private String cpf;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
    @Column(nullable = false) // Garante que a senha não seja nula no banco
    private String senha; // Mantido como 'senha' para consistência

    @Builder.Default
    @Column(name = "ativo", nullable = false)
    private boolean ativo = true; // Permite ativar/desativar o usuário

    @Builder.Default
    @ElementCollection(targetClass = Roles.class, fetch = FetchType.EAGER) // Roles via Enum, EAGER fetch para carregar junto
    @CollectionTable(name = "usuario_roles", joinColumns = @JoinColumn(name = "usuario_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Set<Roles> roles = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY) // Mapeamento com Agendamento
    @JsonBackReference // Evita loops infinitos na serialização JSON
    private Set<Agendamento> agendamentos = new HashSet<>();

    // --- Métodos da interface UserDetails ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Mapeia os enums Roles para SimpleGrantedAuthority
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getAuthority()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return senha; // Retorna o campo 'senha'
    }

    @Override
    public String getUsername() {
        return email; // O email será o nome de usuário para login
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Define como true por padrão; pode ser implementado com lógica de expiração
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Define como true por padrão; pode ser implementado com lógica de bloqueio
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Define como true por padrão; pode ser implementado com lógica de expiração de credenciais
    }

    @Override
    public boolean isEnabled() {
        return ativo; // A conta está habilitada se 'ativo' for true
    }

    // --- Métodos utilitários para Roles ---

    public List<String> getRolesAsStrings() {
        return this.roles.stream()
                .map(Roles::getAuthority)
                .collect(Collectors.toList());
    }

    public void adicionarRole(Roles role) {
        roles.add(role);
    }

    public void removerRole(Roles role) {
        roles.remove(role);
    }

    public boolean temRole(Roles role) {
        return roles.contains(role);
    }

    // --- Equals e HashCode otimizados ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario)) return false;
        Usuario usuario = (Usuario) o;
        // Usa id e email para comparação de igualdade, pois são únicos
        return Objects.equals(id, usuario.id) &&
               Objects.equals(email, usuario.email);
    }

    @Override
    public int hashCode() {
        // Gera o hash baseado em id e email
        return Objects.hash(id, email);
    }
}