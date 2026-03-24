package com.nexus.exception;

import com.nexus.model.Task;

public class NexusValidationException extends RuntimeException {
    public NexusValidationException(String message) {
        super(message);
        Task.incrementTotalValidationErrors();
        // Dica para o aluno: Incrementar contador global de erros aqui? 
        // Ou melhor deixar para a Task gerenciar.
    }
}