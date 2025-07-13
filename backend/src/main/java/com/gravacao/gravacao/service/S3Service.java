package com.gravacao.gravacao.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.UUID;

@Service
public class S3Service {

    @Autowired
    private S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    /**
     * Faz upload de um arquivo para o S3 e retorna a URL pública
     */
    public String uploadFile(MultipartFile file, String tipoEvento) throws IOException {
        // Gera um nome único para o arquivo
        String fileName = generateFileName(file.getOriginalFilename(), tipoEvento);

        try {
            // Cria a requisição de upload
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .acl(ObjectCannedACL.PUBLIC_READ) // Torna o arquivo público
                    .build();

            // Faz o upload
            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            // Retorna a URL pública do arquivo
            return getPublicUrl(fileName);

        } catch (S3Exception e) {
            throw new RuntimeException("Erro ao fazer upload para S3: " + e.getMessage(), e);
        }
    }

    /**
     * Deleta um arquivo do S3
     */
    public void deleteFile(String fileName) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
        } catch (S3Exception e) {
            throw new RuntimeException("Erro ao deletar arquivo do S3: " + e.getMessage(), e);
        }
    }

    /**
     * Verifica se um arquivo existe no S3
     */
    public boolean fileExists(String fileName) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            s3Client.headObject(headObjectRequest);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (S3Exception e) {
            throw new RuntimeException("Erro ao verificar existência do arquivo: " + e.getMessage(), e);
        }
    }

    /**
     * Gera um nome único para o arquivo
     */
    private String generateFileName(String originalFileName, String tipoEvento) {
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        String uuid = UUID.randomUUID().toString();
        String sanitizedTipoEvento = tipoEvento != null ? tipoEvento.toLowerCase().replaceAll("[^a-z0-9]", "")
                : "geral";

        return String.format("imagens/%s/%s%s", sanitizedTipoEvento, uuid, extension);
    }

    /**
     * Constrói a URL pública do arquivo
     */
    private String getPublicUrl(String fileName) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, fileName);
    }

    /**
     * Extrai o nome do arquivo da URL
     */
    public String extractFileNameFromUrl(String url) {
        if (url == null || !url.contains(bucketName)) {
            return null;
        }

        String baseUrl = String.format("https://%s.s3.%s.amazonaws.com/", bucketName, region);
        return url.replace(baseUrl, "");
    }
}
