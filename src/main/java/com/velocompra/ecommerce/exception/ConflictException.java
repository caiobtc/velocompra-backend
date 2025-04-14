package com.velocompra.ecommerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT) // Retorna HTTP 409 quando lançada
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
