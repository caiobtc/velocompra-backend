package com.velocompra.ecommerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceção personalizada que é lançada quando ocorre um conflito no sistema.
 * Esta exceção é marcada com a anotação {@link ResponseStatus} para retornar um código HTTP 409 (CONFLICT) quando for lançada.
 * O código HTTP 409 é usado para indicar que a solicitação não pôde ser processada devido a um conflito com o estado atual do recurso.
 */
@ResponseStatus(HttpStatus.CONFLICT) // Retorna HTTP 409 quando lançada
public class ConflictException extends RuntimeException {

    /**
     * Construtor da exceção ConflictException.
     * Recebe uma mensagem de erro que será associada à exceção.
     *
     * @param message A mensagem que descreve o motivo do conflito.
     */
    public ConflictException(String message) {
        super(message);
    }
}
