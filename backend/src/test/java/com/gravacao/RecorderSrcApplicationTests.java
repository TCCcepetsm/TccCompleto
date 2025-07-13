package com.gravacao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import java.util.List; // Importar List
import static org.junit.jupiter.api.Assertions.assertNotNull;
import com.gravacao.controller.entity.Usuario;
import com.gravacao.repository.UsuarioRepository;

import static org.assertj.core.api.Assertions.assertThat;

// Para usar com H2 (sem Docker) - remova @Testcontainers
@SpringBootTest
@ActiveProfiles("test")
class RecorderSrcApplicationTests {

    @Autowired
    private ApplicationContext context;

    @Test
    void contextLoads() {
        assertThat(context).isNotNull();
    }

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void testFindAllUsuariosWithRoles() {
        List<Usuario> usuarios = usuarioRepository.findAllWithRoles();
        assertNotNull(usuarios);
    }

    @Test
    void testDatabaseConnection() {
        assertThat(context.getEnvironment().getProperty("spring.datasource.url"))
                .isNotNull();
    }
}