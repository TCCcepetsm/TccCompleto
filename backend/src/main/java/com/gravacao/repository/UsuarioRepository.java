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

    // Verificação de existência
    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);
    
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM Usuario u WHERE lower(u.email) = lower(:email)")
    boolean existsByEmailIgnoreCase(@Param("email") String email);

    // Buscas básicas
    Optional<Usuario> findByEmail(String email);
    
    // Buscas com roles - VERSÃO CORRIGIDA
    @EntityGraph(attributePaths = "roles")
    @Query("SELECT DISTINCT u FROM Usuario u LEFT JOIN FETCH u.roles WHERE u.id = :id")
    Optional<Usuario> findByIdWithRoles(@Param("id") Long id);
    
    @EntityGraph(attributePaths = "roles")
    @Query("SELECT DISTINCT u FROM Usuario u LEFT JOIN FETCH u.roles WHERE u.email = :email")
    Optional<Usuario> findByEmailWithRoles(@Param("email") String email);
    
    @Query("SELECT DISTINCT u FROM Usuario u LEFT JOIN FETCH u.roles")
    List<Usuario> findAllWithRoles();

    // Operação de atualização
    @Modifying
    @Transactional
    @Query("UPDATE Usuario u SET u.ultimoAcesso = CURRENT_TIMESTAMP WHERE u.id = :id")
    void registrarUltimoAcesso(@Param("id") Long id);

    // Consulta nativa
    @Query(value = "SELECT u.* FROM usuarios u JOIN usuario_roles ur ON u.id = ur.usuario_id WHERE lower(u.email) = lower(:email)", nativeQuery = true)
    Optional<Usuario> findByEmailWithRolesNative(@Param("email") String email);
}