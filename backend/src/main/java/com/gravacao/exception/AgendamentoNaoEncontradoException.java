package com.gravacao.exception;

public class AgendamentoNaoEncontradoException extends RuntimeException {
    public AgendamentoNaoEncontradoException(String message) {
        super(message);
    }

    public AgendamentoNaoEncontradoException(Long id) {
        super("Agendamento não encontrado com ID: " + id);
    }
}