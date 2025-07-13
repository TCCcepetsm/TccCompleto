package com.gravacao.agendamento.service;

import com.gravacao.agendamento.model.Agendamento;
import com.gravacao.agendamento.repository.AgendamentoRepository;
import com.gravacao.auth.model.User;
import com.gravacao.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AgendamentoService {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private UserRepository userRepository;

    public Agendamento criarAgendamento(Long userId, LocalDateTime dataHora, String tipoEvento, String observacoes) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Agendamento agendamento = new Agendamento();
        agendamento.setUser(user);
        agendamento.setDataHora(dataHora);
        agendamento.setTipoEvento(tipoEvento);
        agendamento.setObservacoes(observacoes);
        agendamento.setStatus(Agendamento.StatusAgendamento.PENDENTE);

        return agendamentoRepository.save(agendamento);
    }

    public List<Agendamento> buscarTodosAgendamentos() {
        return agendamentoRepository.findAll();
    }

    public List<Agendamento> buscarAgendamentosPorUsuario(Long userId) {
        return agendamentoRepository.findByUserId(userId);
    }

    public Optional<Agendamento> buscarAgendamentoPorId(Long id) {
        return agendamentoRepository.findById(id);
    }

    public Agendamento atualizarStatusAgendamento(Long id, Agendamento.StatusAgendamento novoStatus) {
        Agendamento agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));
        agendamento.setStatus(novoStatus);
        return agendamentoRepository.save(agendamento);
    }

    public void deletarAgendamento(Long id) {
        agendamentoRepository.deleteById(id);
    }
}

