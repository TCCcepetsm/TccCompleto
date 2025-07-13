package com.gravacao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@SpringBootApplication
@EnableAsync
@RestControllerAdvice
public class GravacaoApplication {
    public static void main(String[] args) {
        SpringApplication.run(GravacaoApplication.class, args);
    }
}