package com.velocompra.ecommerce.validacao;

import org.springframework.stereotype.Component;

/**
 * Componente responsável pela validação de CPF.
 * Este componente verifica se um CPF fornecido é válido, aplicando as regras de validação padrão.
 */
@Component
public class CpfValidador {

    /**
     * Valida um CPF com base em sua estrutura e nos cálculos dos dígitos verificadores.
     *
     * <p>O CPF é validado removendo caracteres não numéricos, verificando se possui 11 dígitos,
     * e se todos os dígitos não são iguais. Depois, o primeiro e o segundo dígitos verificadores são
     * calculados e comparados com os valores presentes no CPF.</p>
     *
     * @param cpf O CPF a ser validado, que pode conter caracteres não numéricos (será filtrado).
     * @return {@code true} se o CPF for válido, {@code false} caso contrário.
     */
    public boolean isValid(String cpf) {
        // Remover caracteres não numéricos
        cpf = cpf.replaceAll("[^\\d]", "");

        // Verificar se tem 11 dígitos
        if (cpf.length() != 11) {
            return false;
        }

        // Verificar se todos os dígitos são iguais
        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        // Calcular primeiro dígito verificador
        int soma = 0;
        for (int i = 0; i < 9; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
        }

        int primeiroDigito = 11 - (soma % 11);
        if (primeiroDigito >= 10) {
            primeiroDigito = 0;
        }

        // Verificar o primeiro dígito
        if (primeiroDigito != Character.getNumericValue(cpf.charAt(9))) {
            return false;
        }

        // Calcular segundo dígito verificador
        soma = 0;
        for (int i = 0; i < 10; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
        }

        int segundoDigito = 11 - (soma % 11);
        if (segundoDigito >= 10) {
            segundoDigito = 0;
        }

        // Verificar o segundo dígito
        return segundoDigito == Character.getNumericValue(cpf.charAt(10));
    }
}
