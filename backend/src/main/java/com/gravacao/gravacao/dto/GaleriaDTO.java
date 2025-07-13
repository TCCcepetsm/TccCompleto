package com.gravacao.gravacao.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class GaleriaDTO {

    private Long id;

    @NotBlank(message = "Título é obrigatório")
    @Size(max = 255, message = "Título deve ter no máximo 255 caracteres")
    private String titulo;

    private String urlGravacao;

    @Size(max = 100, message = "Tipo de evento deve ter no máximo 100 caracteres")
    private String tipoEvento;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataEvento;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dataUpload;

    private String nomeArquivo;
    private Long tamanhoArquivo;
    private String tipoMime;

    // Construtor padrão
    public GaleriaDTO() {
    }

    // Construtor com parâmetros principais
    public GaleriaDTO(String titulo, String tipoEvento, LocalDate dataEvento) {
        this.titulo = titulo;
        this.tipoEvento = tipoEvento;
        this.dataEvento = dataEvento;
    }

    // Construtor completo
    public GaleriaDTO(Long id, String titulo, String urlGravacao, String tipoEvento,
            LocalDate dataEvento, LocalDateTime dataUpload, String nomeArquivo,
            Long tamanhoArquivo, String tipoMime) {
        this.id = id;
        this.titulo = titulo;
        this.urlGravacao = urlGravacao;
        this.tipoEvento = tipoEvento;
        this.dataEvento = dataEvento;
        this.dataUpload = dataUpload;
        this.nomeArquivo = nomeArquivo;
        this.tamanhoArquivo = tamanhoArquivo;
        this.tipoMime = tipoMime;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getUrlGravacao() {
        return urlGravacao;
    }

    public void setUrlGravacao(String urlGravacao) {
        this.urlGravacao = urlGravacao;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public LocalDate getDataEvento() {
        return dataEvento;
    }

    public void setDataEvento(LocalDate dataEvento) {
        this.dataEvento = dataEvento;
    }

    public LocalDateTime getDataUpload() {
        return dataUpload;
    }

    public void setDataUpload(LocalDateTime dataUpload) {
        this.dataUpload = dataUpload;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public Long getTamanhoArquivo() {
        return tamanhoArquivo;
    }

    public void setTamanhoArquivo(Long tamanhoArquivo) {
        this.tamanhoArquivo = tamanhoArquivo;
    }

    public String getTipoMime() {
        return tipoMime;
    }

    public void setTipoMime(String tipoMime) {
        this.tipoMime = tipoMime;
    }

    @Override
    public String toString() {
        return "GravacaoDTO{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", urlGravacao='" + urlGravacao + '\'' +
                ", tipoEvento='" + tipoEvento + '\'' +
                ", dataEvento=" + dataEvento +
                ", dataUpload=" + dataUpload +
                '}';
    }
}
