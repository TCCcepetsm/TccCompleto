package com.gravacao.component;

import com.gravacao.controller.entity.Agendamento;
import com.gravacao.controller.entity.Usuario;
import com.gravacao.controller.entity.enuns.StatusAgendamento;
import com.gravacao.dto.AgendamentoDTO;
import org.springframework.stereotype.Component;

@Component
public class AgendamentoMapper {

    public Agendamento toEntity(AgendamentoDTO dto, Usuario usuario) {
        Agendamento agendamento = new Agendamento();

        agendamento.setUsuario(usuario);
        agendamento.setNome(dto.getNome()); // Usando o dto passado como parâmetro
        agendamento.setEmail(dto.getEmail());
        agendamento.setTelefone(dto.getTelefone());
        agendamento.setPlano(dto.getPlano());
        agendamento.setData(dto.getData());
        agendamento.setHorario(dto.getHorario());
        agendamento.setEsporte(dto.getEsporte());
        agendamento.setLocal(dto.getLocal());
        agendamento.setLatitude(dto.getLatitude());
        agendamento.setLongitude(dto.getLongitude());
        agendamento.setStatus(StatusAgendamento.PENDENTE);

        return agendamento;
    }
}