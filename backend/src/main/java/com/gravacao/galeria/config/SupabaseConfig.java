package com.gravacao.galeria.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SupabaseConfig {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.anon.key}")
    private String supabaseAnonKey;

    @Value("${supabase.service.role.key}")
    private String supabaseServiceRoleKey;

    @Bean("supabaseRestTemplate") // Nome único para o bean
    public RestTemplate supabaseRestTemplate() {
        return new RestTemplate();
    }

    public String getSupabaseUrl() {
        return supabaseUrl;
    }

    public String getSupabaseAnonKey() {
        return supabaseAnonKey;
    }

    public String getSupabaseServiceRoleKey() {
        return supabaseServiceRoleKey;
    }

    public String getStorageUrl() {
        return supabaseUrl + "/storage/v1";
    }

    public String getBucketUrl(String bucketName) {
        return getStorageUrl() + "/object/" + bucketName;
    }
}
