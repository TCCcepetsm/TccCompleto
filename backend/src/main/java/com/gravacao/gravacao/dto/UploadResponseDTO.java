package com.gravacao.gravacao.dto;

import java.util.List;

public class UploadResponseDTO {

    private boolean sucesso;
    private String mensagem;
    private List<ImagemDTO> imagensUpload;
    private int totalGravacoesUpload;

    // Construtor padrão
    public UploadResponseDTO() {
    }

    // Construtor para sucesso
    public UploadResponseDTO(boolean sucesso, String mensagem, List<ImagemDTO> imagensUpload) {
        this.sucesso = sucesso;
        this.mensagem = mensagem;
        this.imagensUpload = imagensUpload;
        this.totalGravacoesUpload = imagensUpload != null ? imagensUpload.size() : 0;
    }

    // Construtor para erro
    public UploadResponseDTO(boolean sucesso, String mensagem) {
        this.sucesso = sucesso;
        this.mensagem = mensagem;
        this.totalGravacoesUpload = 0;
    }

    // Getters e Setters
    public boolean isSucesso() {
        return sucesso;
    }

    public void setSucesso(boolean sucesso) {
        this.sucesso = sucesso;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public List<ImagemDTO> getGravacoesUpload() {
        return imagensUpload;
    }

    public void setGravacoesUpload(List<ImagemDTO> imagensUpload) {
        this.imagensUpload = imagensUpload;
        this.totalGravacoesUpload = imagensUpload != null ? imagensUpload.size() : 0;
    }

    public int getTotalGravacoesUpload() {
        return totalGravacoesUpload;
    }

    public void setTotalGravacoesUpload(int totalGravacoesUpload) {
        this.totalGravacoesUpload = totalGravacoesUpload;
    }

    @Override
    public String toString() {
        return "UploadResponseDTO{" +
                "sucesso=" + sucesso +
                ", mensagem='" + mensagem + '\'' +
                ", totalGravacoesUpload=" + totalGravacoesUpload +
                '}';
    }
}
