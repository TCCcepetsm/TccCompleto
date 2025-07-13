package com.gravacao;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class GravacaoServletInitializer extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        // Configuração adicional para deploy em container
        setRegisterErrorPageFilter(false); // Desativa filtro de erro padrão
        return application.sources(GravacaoApplication.class)
    }
}