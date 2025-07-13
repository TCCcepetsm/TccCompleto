package com.gravacao.gravacao.service;

import com.gravacao.gravacao.dto.GravacaoDTO;
import com.gravacao.gravacao.dto.UploadResponseDTO;
import com.gravacao.gravacao.model.Gravacao;
import com.gravacao.gravacao.repository.GravacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GravacaoService {

    @Autowired
    private GravacaoRepository imagemRepository;

    @Autowired
    private SupabaseStorageService supabaseStorageService;

    /**
     * Faz upload de múltiplas imagens
     */
    public UploadResponseDTO uploadGravacoes(MultipartFile[] files, String tipoEvento, LocalDate dataEvento) {
        List<GravacaoDTO> imagensUpload = new ArrayList<>();
        List<String> erros = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                if (file.isEmpty()) {
                    erros.add("Arquivo vazio encontrado");
                    continue;
                }

                // Valida o tipo de arquivo
                if (!isValidImageFile(file)) {
                    erros.add("Arquivo " + file.getOriginalFilename() + " não é uma imagem válida");
                    continue;
                }

                // Faz upload para Supabase Storage
                String urlGravacao = supabaseStorageService.uploadFile(file, tipoEvento);

                // Cria e salva a entidade no banco
                Gravacao imagem = new Gravacao();
                imagem.setTitulo(generateTitleFromFileName(file.getOriginalFilename()));
                imagem.setUrlGravacao(urlGravacao);
                imagem.setTipoEvento(tipoEvento);
                imagem.setDataEvento(dataEvento);
                imagem.setNomeArquivo(file.getOriginalFilename());
                imagem.setTamanhoArquivo(file.getSize());
                imagem.setTipoMime(file.getContentType());

                Gravacao imagemSalva = imagemRepository.save(imagem);
                imagensUpload.add(convertToDTO(imagemSalva));

            } catch (Exception e) {
                erros.add("Erro ao processar arquivo " + file.getOriginalFilename() + ": " + e.getMessage());
            }
        }

        // Prepara a resposta
        if (imagensUpload.isEmpty()) {
            String mensagemErro = "Nenhuma imagem foi enviada com sucesso.";
            if (!erros.isEmpty()) {
                mensagemErro += " Erros: " + String.join("; ", erros);
            }
            return new UploadResponseDTO(false, mensagemErro);
        } else {
            String mensagem = imagensUpload.size() + " imagem(ns) enviada(s) com sucesso.";
            if (!erros.isEmpty()) {
                mensagem += " Alguns arquivos falharam: " + String.join("; ", erros);
            }
            return new UploadResponseDTO(true, mensagem, imagensUpload);
        }
    }

    /**
     * Faz upload de uma única imagem
     */
    public GravacaoDTO uploadGravacao(MultipartFile file, String titulo, String tipoEvento) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Arquivo vazio");
            }

            // Valida o tipo de arquivo
            if (!isValidImageFile(file)) {
                throw new RuntimeException("Arquivo não é uma imagem válida");
            }

            // Faz upload para Supabase Storage
            String urlGravacao = supabaseStorageService.uploadFile(file, tipoEvento);

            // Cria e salva a entidade no banco
            Gravacao imagem = new Gravacao();
            imagem.setTitulo(titulo != null && !titulo.trim().isEmpty() ? 
                titulo : generateTitleFromFileName(file.getOriginalFilename()));
            imagem.setUrlGravacao(urlGravacao);
            imagem.setTipoEvento(tipoEvento);
            imagem.setDataEvento(LocalDate.now());
            imagem.setNomeArquivo(file.getOriginalFilename());
            imagem.setTamanhoArquivo(file.getSize());
            imagem.setTipoMime(file.getContentType());

            Gravacao imagemSalva = imagemRepository.save(imagem);
            return convertToDTO(imagemSalva);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao fazer upload da imagem: " + e.getMessage(), e);
        }
    }

    /**
     * Busca todas as imagens
     */
    public List<GravacaoDTO> buscarTodasGravacoes() {
        List<Gravacao> imagens = imagemRepository.findAllByOrderByDataUploadDesc();
        return imagens.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca imagens por tipo de evento
     */
    public List<GravacaoDTO> buscarGravacoesPorTipo(String tipoEvento) {
        List<Gravacao> imagens = imagemRepository.findByTipoEventoIgnoreCaseOrderByDataUploadDesc(tipoEvento);
        return imagens.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca imagens recentes
     */
    public List<GravacaoDTO> buscarGravacoesRecentes(int limite) {
        List<Gravacao> imagens = imagemRepository.findRecentImages(limite);
        return imagens.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca uma imagem por ID
     */
    public Optional<GravacaoDTO> buscarGravacaoPorId(Long id) {
        Optional<Gravacao> imagem = imagemRepository.findById(id);
        return imagem.map(this::convertToDTO);
    }

    /**
     * Deleta uma imagem
     */
    public boolean deletarGravacao(Long id) {
        Optional<Gravacao> imagemOpt = imagemRepository.findById(id);
        if (imagemOpt.isPresent()) {
            Gravacao imagem = imagemOpt.get();
            
            try {
                // Deleta do Supabase Storage
                String fileName = supabaseStorageService.extractFileNameFromUrl(imagem.getUrlGravacao());
                if (fileName != null) {
                    supabaseStorageService.deleteFile(fileName);
                }
                
                // Deleta do banco
                imagemRepository.delete(imagem);
                return true;
            } catch (Exception e) {
                throw new RuntimeException("Erro ao deletar imagem: " + e.getMessage(), e);
            }
        }
        return false;
    }

    /**
     * Valida se o arquivo é uma imagem válida
     */
    private boolean isValidImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (
                contentType.equals("image/jpeg") ||
                contentType.equals("image/jpg") ||
                contentType.equals("image/png") ||
                contentType.equals("image/gif") ||
                contentType.equals("image/webp")
        );
    }

    /**
     * Gera um título baseado no nome do arquivo
     */
    private String generateTitleFromFileName(String fileName) {
        if (fileName == null) {
            return "Gravacao sem título";
        }
        
        // Remove a extensão
        String nameWithoutExtension = fileName.contains(".") ? 
                fileName.substring(0, fileName.lastIndexOf(".")) : fileName;
        
        // Substitui underscores e hífens por espaços e capitaliza
        return nameWithoutExtension
                .replaceAll("[_-]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    /**
     * Converte entidade para DTO
     */
    private GravacaoDTO convertToDTO(Gravacao imagem) {
        return new GravacaoDTO(
                imagem.getId(),
                imagem.getTitulo(),
                imagem.getUrlGravacao(),
                imagem.getTipoEvento(),
                imagem.getDataEvento(),
                imagem.getDataUpload(),
                imagem.getNomeArquivo(),
                imagem.getTamanhoArquivo(),
                imagem.getTipoMime()
        );
    }
}

