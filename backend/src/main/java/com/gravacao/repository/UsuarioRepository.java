package com.gravacao.repository;

import com.gravacao.controller.entity.Usuario;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Verificação de existência por email (case-sensitive)
    boolean existsByEmail(String email);
    
    // Verificação de existência por email (case-insensitive)
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM Usuario u WHERE lower(u.email) = lower(:email)")
    boolean existsByEmailIgnoreCase(@Param("email") String email);

    // Verificação de existência por CPF
    boolean existsByCpf(String cpf);

    // Busca por ID com roles (EntityGraph)
    @EntityGraph(attributePaths = "roles")
    Optional<Usuario> findByIdWithRoles(Long id);

    // Busca por email com roles (EntityGraph)
    @EntityGraph(attributePaths = "roles")
    Optional<Usuario> findByEmailWithRoles(String email);

    // Busca por email básica (sem roles)
    Optional<Usuario> findByEmail(String email);

    // Busca todos com roles (EntityGraph)
    @EntityGraph(attributePaths = "roles")
    List<Usuario> findAllWithRoles();

    // Atualização de último acesso
    @Modifying
    @Transactional
    @Query("UPDATE Usuario u SET u.ultimoAcesso = CURRENT_TIMESTAMP WHERE u.id = :id")
    void registrarUltimoAcesso(@Param("id") Long id);

    // Consulta nativa otimizada para PostgreSQL
    @Query(value = """
            SELECT u.* FROM usuarios u
            JOIN usuario_roles ur ON u.id = ur.usuario_id
            JOIN roles r ON ur.role_id = r.id
            WHERE lower(u.email) = lower(:email)
            """, nativeQuery = true)
    Optional<Usuario> findByEmailWithRolesNative(@Param("email") String email);
}