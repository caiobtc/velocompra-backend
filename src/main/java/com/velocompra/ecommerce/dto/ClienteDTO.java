package com.velocompra.ecommerce.dto;

import com.velocompra.ecommerce.model.Cliente;
import com.velocompra.ecommerce.model.EnderecoFaturamento;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Data Transfer Object (DTO) para representar um cliente no sistema.
 * Contém os dados essenciais do cliente, como nome completo, e-mail, CPF, data de nascimento, gênero,
 * e os endereços de faturamento e de entrega.
 */
@Data
public class ClienteDTO {

    /**
     * Nome completo do cliente.
     */
    private String nomeCompleto;

    /**
     * Endereço de e-mail do cliente.
     */
    private String email;

    /**
     * CPF do cliente.
     */
    private String cpf;

    /**
     * Data de nascimento do cliente.
     */
    private LocalDate dataNascimento;

    /**
     * Gênero do cliente.
     */
    private String genero;

    /**
     * Endereço de faturamento do cliente, representado por um objeto {@link EnderecoDTO}.
     */
    private EnderecoDTO enderecoFaturamento;

    /**
     * Lista de endereços de entrega do cliente, representados por objetos {@link EnderecoDTO}.
     */
    private List<EnderecoDTO> enderecosEntrega;

    /**
     * Construtor que converte um objeto {@link Cliente} em um {@link ClienteDTO}.
     * Preenche os campos do DTO com as informações do cliente, incluindo o endereço de faturamento e os endereços de entrega.
     *
     * @param cliente O objeto cliente que será convertido em DTO.
     */
    public ClienteDTO(Cliente cliente) {
        this.nomeCompleto = cliente.getNomeCompleto();
        this.email = cliente.getEmail();
        this.cpf = cliente.getCpf();
        this.dataNascimento = cliente.getDataNascimento();
        this.genero = cliente.getGenero();

        // Preenchendo o endereço de faturamento com base nos dados do cliente
        EnderecoFaturamento fat = cliente.getEnderecoFaturamento();
        if (fat != null) {
            this.enderecoFaturamento = new EnderecoDTO();
            this.enderecoFaturamento.setCep(fat.getCep());
            this.enderecoFaturamento.setLogradouro(fat.getLogradouro());
            this.enderecoFaturamento.setNumero(fat.getNumero());
            this.enderecoFaturamento.setComplemento(fat.getComplemento());
            this.enderecoFaturamento.setBairro(fat.getBairro());
            this.enderecoFaturamento.setCidade(fat.getCidade());
            this.enderecoFaturamento.setUf(fat.getUf());
        }

        // Preenchendo os endereços de entrega com base nos dados do cliente
        if (cliente.getEnderecosEntrega() != null) {
            this.enderecosEntrega = cliente.getEnderecosEntrega().stream()
                    .map(EnderecoDTO::new)
                    .collect(Collectors.toList());
        }
    }

    /**
     * Construtor vazio para o DTO {@link ClienteDTO}.
     * Necessário para inicialização sem dados.
     */
    public ClienteDTO() {
    }
}
