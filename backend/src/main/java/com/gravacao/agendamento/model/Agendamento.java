package com.gravacao.agendamento.model;

import com.gravacao.auth.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "agendamentos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    @Column(nullable = false)
    private String tipoEvento;

    private String observacoes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAgendamento status;

    public enum StatusAgendamento {
        PENDENTE,
        CONFIRMADO,
        CANCELADO,
        CONCLUIDO
    }
}

