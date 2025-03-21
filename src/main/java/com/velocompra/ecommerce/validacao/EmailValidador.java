package com.velocompra.ecommerce.validacao;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

@Component
public class EmailValidador {
    private final String EMAIL = "^[A-Za-z0-9+_.-]+@(.+)$";

    public boolean isValid(String email) {
        return Pattern.matches(EMAIL, email);
    }
}
