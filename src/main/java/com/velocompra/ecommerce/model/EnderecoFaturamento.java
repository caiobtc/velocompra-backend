package com.velocompra.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.velocompra.ecommerce.model.Cliente;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Entity
@Data
public class EnderecoFaturamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Pattern(regexp = "\\d{8}", message = "Cep deve contar com exatamente 8 digitos")
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
    @Pattern(regexp = "[A-Z]{2}", message = "UF deve conter 2 letras maiúsculas")
    private String uf;


    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;
}
