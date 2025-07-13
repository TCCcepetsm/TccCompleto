
package com.gravacao.agendamento.repository;

import com.gravacao.agendamento.model.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {
    List<Agendamento> findByUserId(Long userId);
    List<Agendamento> findByStatus(Agendamento.StatusAgendamento status);
}

