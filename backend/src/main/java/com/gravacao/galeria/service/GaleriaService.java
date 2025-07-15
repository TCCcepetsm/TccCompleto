package com.gravacao.galeria.service;

import com.gravacao.controller.entity.Galeria;
import com.gravacao.galeria.dto.GaleriaDTO;
import com.gravacao.galeria.dto.UploadResponseDTO;
import com.gravacao.galeria.repository.GaleriaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GaleriaService {

    @Autowired
    private GaleriaRepository GaleriaRepository;

    @Autowired
    private SupabaseStorageService supabaseStorageService;

    /**
     * Faz upload de múltiplas imagens
     */
    public UploadResponseDTO uploadGravacoes(MultipartFile[] files, String tipoEvento, LocalDate dataEvento) {
        List<GaleriaDTO> imagensUpload = new ArrayList<>();
        List<String> erros = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                if (file.isEmpty()) {
                    erros.add("Arquivo vazio encontrado");
                    continue;
                }

                // Valida o tipo de arquivo
                if (!isValidImageFile(file)) {
                    erros.add("Arquivo " + file.getOriginalFilename() + " não é uma Galeria válida");
                    continue;
                }

                // Faz upload para Supabase Storage
                String urlGravacao = supabaseStorageService.uploadFile(file, tipoEvento);

                // Cria e salva a entidade no banco
                Galeria galeria = new Galeria();
                galeria.setTitulo(generateTitleFromFileName(file.getOriginalFilename()));
                galeria.setUrlGravacao(urlGravacao);
                galeria.setTipoEvento(tipoEvento);
                galeria.setDataEvento(dataEvento);
                galeria.setNomeArquivo(file.getOriginalFilename());
                galeria.setTamanhoArquivo(file.getSize());
                galeria.setTipoMime(file.getContentType());

                Galeria GaleriaSalva = GaleriaRepository.save(galeria);
                imagensUpload.add(convertToDTO(GaleriaSalva));

            } catch (Exception e) {
                erros.add("Erro ao processar arquivo " + file.getOriginalFilename() + ": " + e.getMessage());
            }
        }

        // Prepara a resposta
        if (imagensUpload.isEmpty()) {
            String mensagemErro = "Nenhuma Galeria foi enviada com sucesso.";
            if (!erros.isEmpty()) {
                mensagemErro += " Erros: " + String.join("; ", erros);
            }
            return new UploadResponseDTO(false, mensagemErro);
        } else {
            String mensagem = imagensUpload.size() + " Galeria(ns) enviada(s) com sucesso.";
            if (!erros.isEmpty()) {
                mensagem += " Alguns arquivos falharam: " + String.join("; ", erros);
            }
            return new UploadResponseDTO(true, mensagem, imagensUpload);
        }
    }

    /**
     * Faz upload de uma única Galeria
     */
    public GaleriaDTO uploadGravacao(MultipartFile file, String titulo, String tipoEvento) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Arquivo vazio");
            }

            // Valida o tipo de arquivo
            if (!isValidImageFile(file)) {
                throw new RuntimeException("Arquivo não é uma Galeria válida");
            }

            // Faz upload para Supabase Storage
            String urlGravacao = supabaseStorageService.uploadFile(file, tipoEvento);

            // Cria e salva a entidade no banco
            Galeria galeria = new Galeria();
            galeria.setTitulo(titulo != null && !titulo.trim().isEmpty() ? titulo
                    : generateTitleFromFileName(file.getOriginalFilename()));
            galeria.setUrlGravacao(urlGravacao);
            galeria.setTipoEvento(tipoEvento);
            galeria.setDataEvento(LocalDate.now());
            galeria.setNomeArquivo(file.getOriginalFilename());
            galeria.setTamanhoArquivo(file.getSize());
            galeria.setTipoMime(file.getContentType());

            Galeria galeriaSalva = GaleriaRepository.save(galeria);
            return convertToDTO(galeriaSalva);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao fazer upload da Galeria: " + e.getMessage(), e);
        }
    }

    /**
     * Busca todas as imagens
     */
    public List<GaleriaDTO> buscarTodasGravacoes() {
        List<Galeria> imagens = GaleriaRepository.findAllByOrderByDataUploadDesc();
        return imagens.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca imagens por tipo de evento
     */
    public List<GaleriaDTO> buscarGravacoesPorTipo(String tipoEvento) {
        List<Galeria> imagens = GaleriaRepository.findByTipoEventoIgnoreCaseOrderByDataUploadDesc(tipoEvento);
        return imagens.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca imagens recentes
     */
    public List<GaleriaDTO> buscarGravacoesRecentes(int limite) {
        List<Galeria> imagens = GaleriaRepository.findRecentImages(limite);
        return imagens.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca uma Galeria por ID
     */
    public Optional<GaleriaDTO> buscarGravacaoPorId(Long id) {
        Optional<Galeria> Galeria = GaleriaRepository.findById(id);
        return Galeria.map(this::convertToDTO);
    }

    /**
     * Deleta uma Galeria
     */
    public boolean deletarGravacao(Long id) {
        Optional<Galeria> GaleriaOpt = GaleriaRepository.findById(id);
        if (GaleriaOpt.isPresent()) {
            Galeria Galeria = GaleriaOpt.get();

            try {
                // Deleta do Supabase Storage
                String fileName = supabaseStorageService.extractFileNameFromUrl(Galeria.getUrlGravacao());
                if (fileName != null) {
                    supabaseStorageService.deleteFile(fileName);
                }

                // Deleta do banco
                GaleriaRepository.delete(Galeria);
                return true;
            } catch (Exception e) {
                throw new RuntimeException("Erro ao deletar Galeria: " + e.getMessage(), e);
            }
        }
        return false;
    }

    /**
     * Valida se o arquivo é uma Galeria válida
     */
    private boolean isValidImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (contentType.equals("image/jpeg") ||
                contentType.equals("image/jpg") ||
                contentType.equals("image/png") ||
                contentType.equals("image/gif") ||
                contentType.equals("image/webp"));
    }

    /**
     * Gera um título baseado no nome do arquivo
     */
    private String generateTitleFromFileName(String fileName) {
        if (fileName == null) {
            return "Gravacao sem título";
        }

        // Remove a extensão
        String nameWithoutExtension = fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf("."))
                : fileName;

        // Substitui underscores e hífens por espaços e capitaliza
        return nameWithoutExtension
                .replaceAll("[_-]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    /**
     * Converte entidade para DTO
     */
    private GaleriaDTO convertToDTO(Galeria Galeria) {
        return new GaleriaDTO(
                Galeria.getId(),
                Galeria.getTitulo(),
                Galeria.getUrlGravacao(),
                Galeria.getTipoEvento(),
                Galeria.getDataEvento(),
                Galeria.getDataUpload(),
                Galeria.getNomeArquivo(),
                Galeria.getTamanhoArquivo(),
                Galeria.getTipoMime());
    }
}
