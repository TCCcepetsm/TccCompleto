package com.gravacao.controller.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@DiscriminatorValue("PROFISSIONAL")
public class Profissional extends Usuario {

    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;

    @Column(name = "registro_profissional", length = 50, unique = true)
    private String registroProfissional;

    @Column(name = "especializacao", length = 100)
    private String especializacao;

    @Column(name = "avaliacao_media")
    private Double avaliacaoMedia;

    @Column(name = "total_avaliacoes")
    private Integer totalAvaliacoes = 0;

    @Column(name = "total_servicos")
    private Integer totalServicosRealizados = 0;

    // Mantendo a implementação original com ElementCollection
    @ElementCollection
    @CollectionTable(
        name = "profissional_equipamentos", 
        joinColumns = @JoinColumn(name = "profissional_id")
    )
    @Column(name = "equipamento")
    private Set<String> equipamentos = new HashSet<>();

    @OneToMany(
        mappedBy = "profissional", 
        cascade = CascadeType.ALL, 
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private Set<Agendamento> agendamentos = new HashSet<>();

    // Métodos de negócio
    public void adicionarEquipamento(String equipamento) {
        this.equipamentos.add(equipamento);
    }

    public void removerEquipamento(String equipamento) {
        this.equipamentos.remove(equipamento);
    }

    public void incrementarServicosRealizados() {
        this.totalServicosRealizados++;
    }

    public void adicionarAvaliacao(double novaAvaliacao) {
        if (this.avaliacaoMedia == null) {
            this.avaliacaoMedia = novaAvaliacao;
            this.totalAvaliacoes = 1;
        } else {
            double somaTotal = this.avaliacaoMedia * this.totalAvaliacoes + novaAvaliacao;
            this.totalAvaliacoes++;
            this.avaliacaoMedia = somaTotal / this.totalAvaliacoes;
        }
    }
}