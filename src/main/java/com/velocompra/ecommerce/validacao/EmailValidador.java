package com.velocompra.ecommerce.validacao;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

/**
 * Componente responsável pela validação de e-mails.
 * Este componente utiliza uma expressão regular para verificar se um e-mail fornecido segue o formato válido de um e-mail.
 */
@Component
public class EmailValidador {

    // Expressão regular para validar o formato do e-mail
    private final String EMAIL = "^[A-Za-z0-9+_.-]+@(.+)$";

    /**
     * Valida se o e-mail fornecido possui um formato válido.
     * O e-mail é validado com base em uma expressão regular, que verifica se ele segue a estrutura padrão de um endereço de e-mail.
     *
     * @param email O e-mail a ser validado.
     * @return {@code true} se o e-mail for válido, {@code false} caso contrário.
     */
    public boolean isValid(String email) {
        return Pattern.matches(EMAIL, email);
    }
}
