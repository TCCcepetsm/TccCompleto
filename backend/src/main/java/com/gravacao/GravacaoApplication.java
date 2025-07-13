package com.gravacao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableAsync
@ComponentScan(basePackages = {"com.gravacao.auth", "com.gravacao.agendamento", "com.gravacao.gravacao", "com.gravacao"})
public class GravacaoApplication {
    public static void main(String[] args) {
        SpringApplication.run(GravacaoApplication.class, args);
    }
}