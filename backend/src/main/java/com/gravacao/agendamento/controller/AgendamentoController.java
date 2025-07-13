
package com.gravacao.agendamento.controller;

import com.gravacao.agendamento.model.Agendamento;
import com.gravacao.agendamento.service.AgendamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/agendamentos")
@CrossOrigin(origins = "*")
public class AgendamentoController {

    @Autowired
    private AgendamentoService agendamentoService;

    @PostMapping
    public ResponseEntity<Agendamento> criarAgendamento(@RequestBody AgendamentoRequestDTO agendamentoRequest) {
        Agendamento novoAgendamento = agendamentoService.criarAgendamento(
                agendamentoRequest.getUserId(),
                agendamentoRequest.getDataHora(),
                agendamentoRequest.getTipoEvento(),
                agendamentoRequest.getObservacoes()
        );
        return ResponseEntity.ok(novoAgendamento);
    }

    @GetMapping
    public ResponseEntity<List<Agendamento>> buscarTodosAgendamentos() {
        return ResponseEntity.ok(agendamentoService.buscarTodosAgendamentos());
    }

    @GetMapping("/usuario/{userId}")
    public ResponseEntity<List<Agendamento>> buscarAgendamentosPorUsuario(@PathVariable Long userId) {
        return ResponseEntity.ok(agendamentoService.buscarAgendamentosPorUsuario(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Agendamento> buscarAgendamentoPorId(@PathVariable Long id) {
        return agendamentoService.buscarAgendamentoPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Agendamento> atualizarStatusAgendamento(@PathVariable Long id, @RequestBody StatusUpdateRequestDTO statusUpdateRequest) {
        try {
            Agendamento agendamentoAtualizado = agendamentoService.atualizarStatusAgendamento(id, statusUpdateRequest.getStatus());
            return ResponseEntity.ok(agendamentoAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAgendamento(@PathVariable Long id) {
        agendamentoService.deletarAgendamento(id);
        return ResponseEntity.noContent().build();
    }

    // DTOs para as requisições
    static class AgendamentoRequestDTO {
        private Long userId;
        private LocalDateTime dataHora;
        private String tipoEvento;
        private String observacoes;

        // Getters e Setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public LocalDateTime getDataHora() { return dataHora; }
        public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
        public String getTipoEvento() { return tipoEvento; }
        public void setTipoEvento(String tipoEvento) { this.tipoEvento = tipoEvento; }
        public String getObservacoes() { return observacoes; }
        public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    }

    static class StatusUpdateRequestDTO {
        private Agendamento.StatusAgendamento status;

        // Getters e Setters
        public Agendamento.StatusAgendamento getStatus() { return status; }
        public void setStatus(Agendamento.StatusAgendamento status) { this.status = status; }
    }
}

