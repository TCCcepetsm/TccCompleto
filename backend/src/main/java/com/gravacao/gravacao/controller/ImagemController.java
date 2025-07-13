package com.gravacao.backend.controller;

import com.gravacao.backend.dto.GravacaoDTO;
import com.gravacao.backend.dto.UploadResponseDTO;
import com.gravacao.backend.service.GravacaoService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/imagens")
@CrossOrigin(origins = "*")
public class GravacaoController {

    @Autowired
    private GravacaoService imagemService;

    /**
     * Endpoint para upload de imagens
     * POST /api/imagens/upload
     */
    @PostMapping("/upload")
    public ResponseEntity<UploadResponseDTO> uploadGravacoes(
            @RequestParam("mediaFiles") MultipartFile[] files,
            @RequestParam("eventType") String tipoEvento,
            @RequestParam("eventDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataEvento) {
        
        try {
            if (files == null || files.length == 0) {
                return ResponseEntity.badRequest()
                        .body(new UploadResponseDTO(false, "Nenhum arquivo foi enviado"));
            }

            UploadResponseDTO response = imagemService.uploadGravacoes(files, tipoEvento, dataEvento);
            
            if (response.isSucesso()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new UploadResponseDTO(false, "Erro interno do servidor: " + e.getMessage()));
        }
    }

    /**
     * Endpoint para buscar todas as imagens
     * GET /api/imagens
     */
    @GetMapping
    public ResponseEntity<List<GravacaoDTO>> buscarTodasGravacoes() {
        try {
            List<GravacaoDTO> imagens = imagemService.buscarTodasGravacoes();
            return ResponseEntity.ok(imagens);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint para buscar imagens por tipo de evento
     * GET /api/imagens/tipo/{tipoEvento}
     */
    @GetMapping("/tipo/{tipoEvento}")
    public ResponseEntity<List<GravacaoDTO>> buscarGravacoesPorTipo(@PathVariable String tipoEvento) {
        try {
            List<GravacaoDTO> imagens = imagemService.buscarGravacoesPorTipo(tipoEvento);
            return ResponseEntity.ok(imagens);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint para buscar imagens recentes
     * GET /api/imagens/recentes?limite=10
     */
    @GetMapping("/recentes")
    public ResponseEntity<List<GravacaoDTO>> buscarGravacoesRecentes(
            @RequestParam(defaultValue = "10") int limite) {
        try {
            if (limite <= 0 || limite > 100) {
                limite = 10; // Valor padrão seguro
            }
            
            List<GravacaoDTO> imagens = imagemService.buscarGravacoesRecentes(limite);
            return ResponseEntity.ok(imagens);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint para buscar uma imagem específica por ID
     * GET /api/imagens/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<GravacaoDTO> buscarGravacaoPorId(@PathVariable Long id) {
        try {
            Optional<GravacaoDTO> imagem = imagemService.buscarGravacaoPorId(id);
            
            if (imagem.isPresent()) {
                return ResponseEntity.ok(imagem.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint para deletar uma imagem
     * DELETE /api/imagens/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarGravacao(@PathVariable Long id) {
        try {
            boolean deletada = imagemService.deletarGravacao(id);
            
            if (deletada) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint de health check
     * GET /api/imagens/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Serviço de imagens está funcionando!");
    }
}

