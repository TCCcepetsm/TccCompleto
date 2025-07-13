package com.gravacao.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuração específica para otimizar a aplicação no Render
 * Inclui estratégias para evitar o sleep mode e otimizar performance
 */
@Configuration
@Profile("prod")
@EnableScheduling
public class RenderConfig {

    private static final Logger logger = LoggerFactory.getLogger(RenderConfig.class);

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean("renderRestTemplate") // Nome único para o bean
    public RestTemplate renderRestTemplate() {
        return new RestTemplate();
    }

    /**
     * Ping automático para evitar que o serviço entre em sleep mode
     * Executa a cada 14 minutos (Render coloca em sleep após 15 minutos de
     * inatividade)
     */
    @Scheduled(fixedRate = 840000) // 14 minutos em milissegundos
    public void keepAlive() {
        try {
            String url = "http://localhost:" + serverPort + "/api/health";
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);
            logger.info("Keep-alive ping successful: {}", response);
        } catch (Exception e) {
            logger.warn("Keep-alive ping failed: {}", e.getMessage());
        }
    }

    /**
     * Log de status da aplicação a cada hora
     */
    @Scheduled(fixedRate = 3600000) // 1 hora
    public void logStatus() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;

        logger.info("Application Status - Used Memory: {}MB, Free Memory: {}MB, Total Memory: {}MB",
                usedMemory / 1024 / 1024,
                freeMemory / 1024 / 1024,
                totalMemory / 1024 / 1024);
    }
}
