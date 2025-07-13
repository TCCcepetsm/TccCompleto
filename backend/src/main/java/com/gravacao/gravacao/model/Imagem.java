package com.gravacao.gravacao.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "imagens")
public class Imagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Título é obrigatório")
    @Size(max = 255, message = "Título deve ter no máximo 255 caracteres")
    @Column(name = "titulo", nullable = false)
    private String titulo;

    @NotBlank(message = "URL da imagem é obrigatória")
    @Size(max = 2048, message = "URL deve ter no máximo 2048 caracteres")
    @Column(name = "url_imagem", nullable = false, length = 2048)
    private String urlGravacao;

    @Size(max = 100, message = "Tipo de evento deve ter no máximo 100 caracteres")
    @Column(name = "tipo_evento")
    private String tipoEvento;

    @Column(name = "data_evento")
    private LocalDate dataEvento;

    @Column(name = "data_upload", nullable = false)
    private LocalDateTime dataUpload;

    @Size(max = 255, message = "Nome do arquivo deve ter no máximo 255 caracteres")
    @Column(name = "nome_arquivo")
    private String nomeArquivo;

    @Column(name = "tamanho_arquivo")
    private Long tamanhoArquivo;

    @Size(max = 50, message = "Tipo MIME deve ter no máximo 50 caracteres")
    @Column(name = "tipo_mime")
    private String tipoMime;

    // Construtor padrão
    public Imagem() {
        this.dataUpload = LocalDateTime.now();
    }

    // Construtor com parâmetros
    public Imagem(String titulo, String urlGravacao, String tipoEvento, LocalDate dataEvento) {
        this();
        this.titulo = titulo;
        this.urlGravacao = urlGravacao;
        this.tipoEvento = tipoEvento;
        this.dataEvento = dataEvento;
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

    @PrePersist
    protected void onCreate() {
        if (dataUpload == null) {
            dataUpload = LocalDateTime.now();
        }
    }

    @Override
    public String toString() {
        return "Imagem{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", urlGravacao='" + urlGravacao + '\'' +
                ", tipoEvento='" + tipoEvento + '\'' +
                ", dataEvento=" + dataEvento +
                ", dataUpload=" + dataUpload +
                '}';
    }
}
