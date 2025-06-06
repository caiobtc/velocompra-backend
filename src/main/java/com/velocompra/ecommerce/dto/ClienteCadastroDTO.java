package com.velocompra.ecommerce.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

/**
 * Data Transfer Object (DTO) utilizado para cadastrar um novo cliente no sistema.
 * Contém informações pessoais e de endereço do cliente, como nome, CPF, e-mail, data de nascimento,
 * gênero, senha, endereço de faturamento e endereços de entrega.
 */
@Getter
@Setter
public class ClienteCadastroDTO {

    /**
     * Nome completo do cliente.
     * O nome deve conter pelo menos duas palavras com no mínimo 3 letras cada.
     * O nome é obrigatório.
     */
    @NotBlank
    @Pattern(regexp = "^(\\w{3,})\\s(\\w{3,})$", message = "O nome deve conter pelo menos duas palavras com 3 letras cada.")
    private String nome;

    /**
     * CPF do cliente.
     * O CPF deve conter exatamente 11 dígitos numéricos.
     * Este campo é obrigatório.
     */
    @NotBlank
    @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos.")
    private String cpf;

    /**
     * Endereço de e-mail do cliente.
     * O e-mail deve ser válido e obrigatório.
     */
    @Email
    @NotBlank
    private String email;

    /**
     * Data de nascimento do cliente.
     * A data de nascimento é obrigatória.
     */
    @NotNull
    private LocalDate dataNascimento;

    /**
     * Gênero do cliente.
     * Este campo é obrigatório.
     */
    @NotBlank
    private String genero;

    /**
     * Senha do cliente.
     * A senha deve ter no mínimo 6 caracteres.
     * Este campo é obrigatório.
     */
    @NotBlank
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.")
    private String senha;

    /**
     * Confirmação da senha.
     * O valor deste campo deve ser igual ao campo senha.
     * Este campo é obrigatório.
     */
    @NotBlank
    private String confirmarSenha;

    /**
     * Endereço de faturamento do cliente.
     * Este campo é obrigatório e representa o endereço principal de faturamento.
     */
    @NotNull
    private EnderecoDTO enderecoFaturamento;

    /**
     * Lista de endereços de entrega do cliente.
     * Este campo não é obrigatório e contém os endereços de entrega cadastrados pelo cliente.
     */
    private List<EnderecoDTO> enderecosEntrega;
}
