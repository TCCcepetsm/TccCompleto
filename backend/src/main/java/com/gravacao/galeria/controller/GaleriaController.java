package com.gravacao.galeria.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.gravacao.galeria.dto.GaleriaDTO;
import com.gravacao.galeria.dto.UploadResponseDTO;
import com.gravacao.galeria.service.GaleriaService;

@RestController
@RequestMapping("/galeria")
@CrossOrigin(origins = "*")
public class GaleriaController {

    @Autowired
    private GaleriaService galeriaService;

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

            UploadResponseDTO response = galeriaService.uploadGravacoes(files, tipoEvento, dataEvento);

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
    public ResponseEntity<List<GaleriaDTO>> buscarTodasGravacoes() {
        try {
            List<GaleriaDTO> imagens = galeriaService.buscarTodasGravacoes();
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
    public ResponseEntity<List<GaleriaDTO>> buscarGravacoesPorTipo(@PathVariable String tipoEvento) {
        try {
            List<GaleriaDTO> imagens = galeriaService.buscarGravacoesPorTipo(tipoEvento);
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
    public ResponseEntity<List<GaleriaDTO>> buscarGravacoesRecentes(
            @RequestParam(defaultValue = "10") int limite) {
        try {
            if (limite <= 0 || limite > 100) {
                limite = 10; // Valor padrão seguro
            }

            List<GaleriaDTO> imagens = galeriaService.buscarGravacoesRecentes(limite);
            return ResponseEntity.ok(imagens);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint para buscar uma galeria específica por ID
     * GET /api/imagens/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<GaleriaDTO> buscarGravacaoPorId(@PathVariable Long id) {
        try {
            Optional<GaleriaDTO> galeria = galeriaService.buscarGravacaoPorId(id);

            if (galeria.isPresent()) {
                return ResponseEntity.ok(galeria.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint para deletar uma galeria
     * DELETE /api/imagens/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarGravacao(@PathVariable Long id) {
        try {
            boolean deletada = galeriaService.deletarGravacao(id);

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
