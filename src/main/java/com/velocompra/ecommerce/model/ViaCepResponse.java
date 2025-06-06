package com.velocompra.ecommerce.model;

import lombok.Data;

/**
 * Representa a resposta da API ViaCep para uma consulta de endereço.
 * Esta classe mapeia os dados retornados pela API ViaCep, incluindo informações como CEP,
 * logradouro, complemento, bairro, cidade (localidade), UF e um indicador de erro.
 */
@Data
public class ViaCepResponse {

    /**
     * O CEP do endereço retornado pela API.
     * Deve conter exatamente 8 dígitos numéricos.
     */
    private String cep;

    /**
     * O logradouro (rua, avenida, etc.) do endereço retornado pela API.
     */
    private String logradouro;

    /**
     * Complemento do endereço, como número do apartamento, bloco, etc.
     * Este campo pode ser nulo, dependendo da resposta da API.
     */
    private String complemento;

    /**
     * O bairro do endereço retornado pela API.
     */
    private String bairro;

    /**
     * A localidade (cidade) do endereço retornado pela API.
     */
    private String localidade;

    /**
     * A unidade federativa (UF) do endereço retornado pela API.
     * Representada por duas letras maiúsculas (ex: "SP" para São Paulo).
     */
    private String uf;

    /**
     * Indicador de erro na consulta do CEP.
     * Se for {@code true}, significa que houve um erro ao buscar o CEP.
     * Se for {@code false}, a consulta foi realizada com sucesso.
     */
    private Boolean erro;
}
