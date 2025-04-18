package com.velocompra.ecommerce.dto;

import com.velocompra.ecommerce.model.Cliente;
import com.velocompra.ecommerce.model.Endereco;
import com.velocompra.ecommerce.model.EnderecoFaturamento;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ClienteDTO {
    private String nomeCompleto;
    private String email;
    private String cpf;
    private LocalDate dataNascimento;
    private String genero;
    private EnderecoDTO enderecoFaturamento;
    private List<EnderecoDTO> enderecosEntrega;

    public ClienteDTO(Cliente cliente) {
        this.nomeCompleto = cliente.getNomeCompleto();
        this.email = cliente.getEmail();
        this.cpf = cliente.getCpf();
        this.dataNascimento = cliente.getDataNascimento();
        this.genero = cliente.getGenero();

        // Endereço de faturamento (entidade separada agora)
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

        // Endereços de entrega (continua igual)
        if (cliente.getEnderecosEntrega() != null) {
            this.enderecosEntrega = cliente.getEnderecosEntrega().stream()
                    .map(EnderecoDTO::new)
                    .collect(Collectors.toList());
        }
    }

    public ClienteDTO() {
    }
}
