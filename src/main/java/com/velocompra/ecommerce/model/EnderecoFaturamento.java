package com.velocompra.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.velocompra.ecommerce.model.Cliente;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * Representa um endereço de faturamento associado a um cliente.
 * A classe contém os dados necessários para um endereço de faturamento, como CEP, logradouro, número, complemento, bairro, cidade, UF,
 * e estabelece o relacionamento com a entidade {@link Cliente}.
 */
@Entity
@Data
public class EnderecoFaturamento {

    /**
     * Identificador único do endereço de faturamento.
     * Gerado automaticamente pelo banco de dados.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * CEP do endereço de faturamento, que deve conter exatamente 8 dígitos numéricos.
     * Este campo é obrigatório.
     */
    @NotBlank
    @Pattern(regexp = "\\d{8}", message = "Cep deve contar com exatamente 8 digitos")
    private String cep;

    /**
     * Logradouro do endereço de faturamento (rua, avenida, etc.).
     * Este campo é obrigatório.
     */
    @NotBlank
    private String logradouro;

    /**
     * Número do endereço de faturamento (número da casa, apartamento, etc.).
     * Este campo é obrigatório.
     */
    @NotBlank
    private String numero;

    /**
     * Complemento do endereço, como apartamento, bloco, etc.
     * Este campo não é obrigatório.
     */
    private String complemento;

    /**
     * Bairro do endereço de faturamento.
     * Este campo é obrigatório.
     */
    @NotBlank
    private String bairro;

    /**
     * Cidade do endereço de faturamento.
     * Este campo é obrigatório.
     */
    @NotBlank
    private String cidade;

    /**
     * UF (Unidade Federativa) do endereço de faturamento, representada por duas letras maiúsculas.
     * Este campo é obrigatório.
     */
    @NotBlank
    @Pattern(regexp = "[A-Z]{2}", message = "UF deve conter 2 letras maiúsculas")
    private String uf;

    /**
     * Relacionamento de muitos para um (Many-to-One) com a entidade {@link Cliente}.
     * Cada endereço de faturamento está associado a um cliente específico.
     */
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;
}
