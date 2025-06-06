package com.velocompra.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

/**
 * Representa um endereço de entrega associado a um cliente no sistema.
 * A classe contém os dados do endereço de entrega, incluindo CEP, logradouro, número, complemento, bairro, cidade,
 * UF e se o endereço é o padrão do cliente. Também estabelece o relacionamento com a entidade {@link Cliente}.
 */
@Getter
@Setter
@Entity
@Table(name = "enderecos_entrega")
public class EnderecoEntrega {

    /**
     * Identificador único do endereço de entrega.
     * Gerado automaticamente pelo banco de dados.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Adicionando o campo id

    /**
     * CEP do endereço de entrega, que deve conter exatamente 8 dígitos numéricos.
     * Este campo é obrigatório.
     */
    @NotBlank(message = "O CEP é obrigatório")
    @Pattern(regexp = "\\d{8}", message = "CEP deve conter exatamente 8 dígitos")
    private String cep;

    /**
     * Logradouro do endereço de entrega, como rua, avenida, etc.
     * Este campo é obrigatório.
     */
    @NotBlank(message = "Logradouro é obrigatório")
    private String logradouro;

    /**
     * Número do endereço de entrega, como o número da casa ou prédio.
     * Este campo é obrigatório.
     */
    @NotBlank(message = "Número é obrigatório")
    private String numero;

    /**
     * Complemento do endereço, como apartamento, bloco, etc.
     * Este campo não é obrigatório.
     */
    private String complemento;

    /**
     * Bairro do endereço de entrega.
     * Este campo é obrigatório.
     */
    @NotBlank(message = "Bairro é obrigatório")
    private String bairro;

    /**
     * Cidade do endereço de entrega.
     * Este campo é obrigatório.
     */
    @NotBlank(message = "Cidade é obrigatório")
    private String cidade;

    /**
     * UF (Unidade Federativa) do endereço de entrega, representada por duas letras maiúsculas.
     * Este campo é obrigatório.
     */
    @NotBlank(message = "UF é obrigatório")
    @Pattern(regexp = "[A-Z]{2}", message = "UF deve conter 2 letras maiúsculas")
    private String uf;

    /**
     * Indica se o endereço de entrega é o padrão do cliente.
     * Um cliente pode ter múltiplos endereços de entrega, mas apenas um pode ser marcado como padrão.
     */
    private Boolean padrao;

    /**
     * Relacionamento de muitos para um (Many-to-One) com a entidade {@link Cliente}.
     * Cada endereço de entrega está associado a um cliente específico.
     */
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

}
