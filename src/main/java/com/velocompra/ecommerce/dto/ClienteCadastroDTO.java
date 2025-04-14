package com.velocompra.ecommerce.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;


@Getter
@Setter
public class ClienteCadastroDTO {

    @NotBlank
    @Pattern(regexp = "^(\\w{3,})\\s(\\w{3,})$", message = "O nome deve conter pelo menos duas palavras com 3 letras cada.")
    private String nome;

    @NotBlank
    @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos.")
    private String cpf;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private LocalDate dataNascimento;

    @NotBlank
    private String genero;

    @NotBlank
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.")
    private String senha;

    @NotBlank
    private String confirmarSenha;

    @NotNull
    private EnderecoDTO enderecoFaturamento;

    private List<EnderecoDTO> enderecosEntrega;
}
