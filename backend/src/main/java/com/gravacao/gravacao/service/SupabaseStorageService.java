package com.gravacao.gravacao.service;

import com.gravacao.gravacao.config.SupabaseConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class SupabaseStorageService {
    
    @Autowired
    private SupabaseConfig supabaseConfig;
    
    @Autowired
    private RestTemplate restTemplate;
    
    private static final String BUCKET_NAME = "imagens";
    
    public String uploadFile(MultipartFile file, String tipoEvento) {
        try {
            // Gerar nome único para o arquivo
            String fileName = generateFileName(file.getOriginalFilename(), tipoEvento);
            
            // Preparar headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + supabaseConfig.getSupabaseServiceRoleKey());
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            
            // Preparar body
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return fileName;
                }
            });
            
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            
            // URL para upload
            String uploadUrl = supabaseConfig.getBucketUrl(BUCKET_NAME) + "/" + fileName;
            
            // Fazer upload
            ResponseEntity<String> response = restTemplate.exchange(
                uploadUrl,
                HttpMethod.POST,
                requestEntity,
                String.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.CREATED) {
                // Retornar URL pública do arquivo
                return getPublicUrl(fileName);
            } else {
                throw new RuntimeException("Erro ao fazer upload do arquivo: " + response.getStatusCode());
            }
            
        } catch (IOException e) {
            throw new RuntimeException("Erro ao processar arquivo", e);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao fazer upload para Supabase Storage", e);
        }
    }
    
    public void deleteFile(String fileName) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + supabaseConfig.getSupabaseServiceRoleKey());
            
            HttpEntity<String> requestEntity = new HttpEntity<>(headers);
            
            String deleteUrl = supabaseConfig.getBucketUrl(BUCKET_NAME) + "/" + fileName;
            
            restTemplate.exchange(
                deleteUrl,
                HttpMethod.DELETE,
                requestEntity,
                String.class
            );
            
        } catch (Exception e) {
            throw new RuntimeException("Erro ao deletar arquivo do Supabase Storage", e);
        }
    }
    
    public String getPublicUrl(String fileName) {
        return supabaseConfig.getSupabaseUrl() + "/storage/v1/object/public/" + BUCKET_NAME + "/" + fileName;
    }
    
    private String generateFileName(String originalFileName, String tipoEvento) {
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        
        String sanitizedTipoEvento = tipoEvento.toLowerCase()
            .replaceAll("[^a-z0-9]", "_")
            .replaceAll("_+", "_");
        
        return sanitizedTipoEvento + "/" + UUID.randomUUID().toString() + extension;
    }
    
    public String extractFileNameFromUrl(String url) {
        if (url == null || !url.contains("/storage/v1/object/public/" + BUCKET_NAME + "/")) {
            return null;
        }
        
        String prefix = "/storage/v1/object/public/" + BUCKET_NAME + "/";
        int startIndex = url.indexOf(prefix) + prefix.length();
        return url.substring(startIndex);
    }
}

