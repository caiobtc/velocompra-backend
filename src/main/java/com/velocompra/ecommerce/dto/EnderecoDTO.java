package com.velocompra.ecommerce.dto;

import com.velocompra.ecommerce.model.EnderecoEntrega;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * Data Transfer Object (DTO) utilizado para representar os dados de um endereço de entrega.
 * Este DTO é usado para transferir as informações de um endereço entre as camadas do sistema.
 * Contém os campos de CEP, logradouro, número, complemento, bairro, cidade, UF e um indicador de endereço padrão.
 */
@Data
public class EnderecoDTO {

    /**
     * O CEP do endereço de entrega.
     * Deve conter exatamente 8 dígitos numéricos. Este campo é obrigatório.
     */
    @NotBlank
    @Pattern(regexp = "\\d{8}", message = "Cep deve conter exatamente 8 dígitos")
    private String cep;

    /**
     * O logradouro do endereço de entrega (rua, avenida, etc.).
     * Este campo é obrigatório.
     */
    @NotBlank
    private String logradouro;

    /**
     * O número do endereço de entrega (número da casa, apartamento, etc.).
     * Este campo é obrigatório.
     */
    @NotBlank
    private String numero;

    /**
     * Complemento do endereço de entrega, como apartamento, bloco, etc.
     * Este campo não é obrigatório.
     */
    private String complemento;

    /**
     * O bairro do endereço de entrega.
     * Este campo é obrigatório.
     */
    @NotBlank
    private String bairro;

    /**
     * A cidade do endereço de entrega.
     * Este campo é obrigatório.
     */
    @NotBlank
    private String cidade;

    /**
     * A unidade federativa (UF) do endereço de entrega, representada por duas letras maiúsculas.
     * Este campo é obrigatório.
     */
    @NotBlank
    private String uf;

    /**
     * Indica se este é o endereço de entrega padrão do cliente.
     * Um cliente pode ter vários endereços de entrega, mas somente um pode ser o padrão.
     */
    private boolean padrao;

    /**
     * Construtor vazio do DTO {@link EnderecoDTO}.
     * Necessário para a inicialização do objeto sem dados.
     */
    public EnderecoDTO() {}

    /**
     * Construtor que cria um {@link EnderecoDTO} a partir de um objeto {@link EnderecoEntrega}.
     * Este método é utilizado para converter um endereço de entrega em um DTO.
     *
     * @param enderecoEntrega O objeto {@link EnderecoEntrega} que será convertido para DTO.
     */
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
