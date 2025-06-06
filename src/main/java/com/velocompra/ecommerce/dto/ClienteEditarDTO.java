package com.velocompra.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) utilizado para editar as informações de um cliente no sistema.
 * Contém campos para nome, data de nascimento, gênero, e senha, além de um método para validar e editar dados do cliente.
 */
@Data
public class ClienteEditarDTO {

    /**
     * Nome do cliente.
     * Este campo é obrigatório.
     */
    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    /**
     * Data de nascimento do cliente.
     * Este campo é obrigatório.
     */
    @NotNull(message = "Data de nascimento é obrigatória")
    private LocalDate dataNascimento;

    /**
     * Gênero do cliente.
     * Este campo é obrigatório.
     */
    @NotBlank(message = "Gênero é obrigatório")
    private String genero;

    /**
     * Senha atual do cliente.
     * Usada para validação antes de permitir a alteração da senha.
     * Este campo é opcional.
     */
    private String senhaAtual;

    /**
     * Nova senha do cliente.
     * Este campo é opcional e utilizado para atualizar a senha do cliente.
     */
    private String novaSenha;

    /**
     * Confirmação da nova senha.
     * Este campo é opcional e deve ser igual ao campo novaSenha para validar a alteração da senha.
     */
    private String confirmarSenha;

    /**
     * Método que retorna o endereço de faturamento do cliente.
     * No momento, retorna {@code null}, pois não é utilizado diretamente no DTO de edição.
     *
     * @return O endereço de faturamento (não implementado no momento).
     */
    public EnderecoDTO getEnderecoFaturamento() {
        return null;
    }
}
