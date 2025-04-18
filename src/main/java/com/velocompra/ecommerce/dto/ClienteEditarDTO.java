package com.velocompra.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ClienteEditarDTO {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotNull(message = "Data de nascimento é obrigatória")
    private LocalDate dataNascimento;

    @NotBlank(message = "Gênero é obrigatório")
    private String genero;

    private String senhaAtual;
    private String novaSenha;
    private String confirmarSenha;

    public EnderecoDTO getEnderecoFaturamento() {

        return null;
    }
}
