package com.velocompra.ecommerce.dto;

import com.velocompra.ecommerce.model.EnderecoEntrega;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;


@Data
public class EnderecoDTO {

    @NotBlank
    @Pattern(regexp = "\\d{8}", message = "Cep deve conter exatamente 8 dígitos")
    private String cep;

    @NotBlank
    private String logradouro;

    @NotBlank
    private String numero;

    private String complemento;

    @NotBlank
    private String bairro;

    @NotBlank
    private String cidade;

    @NotBlank
    private String uf;

    private boolean padrao;

    public EnderecoDTO() {}

    public EnderecoDTO(EnderecoEntrega enderecoEntrega) {
        this.cep = enderecoEntrega.getCep();
        this.logradouro = enderecoEntrega.getLogradouro();
        this.numero = enderecoEntrega.getNumero();
        this.complemento = enderecoEntrega.getComplemento();
        this.bairro = enderecoEntrega.getBairro();
        this.cidade = enderecoEntrega.getCidade();
        this.uf = enderecoEntrega.getUf();
    }
}
