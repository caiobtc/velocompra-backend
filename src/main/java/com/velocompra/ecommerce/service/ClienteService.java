package com.velocompra.ecommerce.service;

import com.velocompra.ecommerce.dto.ClienteCadastroDTO;
import com.velocompra.ecommerce.dto.ClienteEditarDTO;
import com.velocompra.ecommerce.dto.EnderecoDTO;
import com.velocompra.ecommerce.exception.ConflictException;
import com.velocompra.ecommerce.model.*;
import com.velocompra.ecommerce.repository.ClienteRepository;
import com.velocompra.ecommerce.repository.EnderecoFaturamentoRepository;
import com.velocompra.ecommerce.util.ViaCepClient;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EnderecoFaturamentoRepository enderecoFaturamentoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ViaCepClient viaCepUtil;

    @Transactional
    public void cadastrar(ClienteCadastroDTO dto) {
        if (clienteRepository.existsByCpf(dto.getCpf())) {
            throw new ConflictException("CPF já cadastrado!");
        }

        if (clienteRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Email já cadastrado!");
        }

        Cliente cliente = new Cliente();
        cliente.setNomeCompleto(dto.getNome());
        cliente.setEmail(dto.getEmail());
        cliente.setCpf(dto.getCpf());
        cliente.setDataNascimento(dto.getDataNascimento());
        cliente.setGenero(dto.getGenero());
        cliente.setSenha(passwordEncoder.encode(dto.getSenha()));

        // Endereço de Faturamento
        EnderecoDTO endDTO = dto.getEnderecoFaturamento();
        EnderecoEntrega viaCep = viaCepUtil.buscarCep(endDTO.getCep());

        EnderecoFaturamento faturamento = new EnderecoFaturamento();
        faturamento.setCep(viaCep.getCep());
        faturamento.setLogradouro(viaCep.getLogradouro());
        faturamento.setBairro(viaCep.getBairro());
        faturamento.setCidade(viaCep.getCidade());
        faturamento.setUf(viaCep.getUf());
        faturamento.setNumero(endDTO.getNumero());
        faturamento.setComplemento(endDTO.getComplemento());

        enderecoFaturamentoRepository.save(faturamento);
        cliente.setEnderecoFaturamento(faturamento);

        // Endereços de Entrega
        List<EnderecoEntrega> enderecosEntrega = dto.getEnderecosEntrega().stream().map(endDTOEntrega -> {
            EnderecoEntrega enderecoEntrega = viaCepUtil.buscarCep(endDTOEntrega.getCep());
            enderecoEntrega.setNumero(endDTOEntrega.getNumero());
            enderecoEntrega.setComplemento(endDTOEntrega.getComplemento());
            return enderecoEntrega;
        }).collect(Collectors.toList());

        cliente.setEnderecosEntrega(enderecosEntrega);

        clienteRepository.save(cliente);
    }

    @Transactional
    public void atualizarDados(String email, ClienteEditarDTO dto) {
        Cliente cliente = clienteRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        cliente.setNomeCompleto(dto.getNome());
        cliente.setDataNascimento(dto.getDataNascimento());
        cliente.setGenero(dto.getGenero());

        EnderecoDTO endDTO = dto.getEnderecoFaturamento();
        if (endDTO != null) {
            EnderecoEntrega viaCep = viaCepUtil.buscarCep(endDTO.getCep());

            EnderecoFaturamento faturamento = new EnderecoFaturamento();
            faturamento.setCep(viaCep.getCep());
            faturamento.setLogradouro(viaCep.getLogradouro());
            faturamento.setBairro(viaCep.getBairro());
            faturamento.setCidade(viaCep.getCidade());
            faturamento.setUf(viaCep.getUf());
            faturamento.setNumero(endDTO.getNumero());
            faturamento.setComplemento(endDTO.getComplemento());

            enderecoFaturamentoRepository.save(faturamento);
            cliente.setEnderecoFaturamento(faturamento);
        }

        if (dto.getSenhaAtual() != null && !dto.getSenhaAtual().isEmpty()) {
            if (!passwordEncoder.matches(dto.getSenhaAtual(), cliente.getSenha())) {
                throw new RuntimeException("Senha atual incorreta");
            }
            if (dto.getNovaSenha() == null || dto.getNovaSenha().isEmpty()) {
                throw new RuntimeException("Nova senha não pode ser vazia");
            }
            cliente.setSenha(passwordEncoder.encode(dto.getNovaSenha()));
        }

        clienteRepository.save(cliente);
    }

    @Transactional
    public void adicionarEnderecoEntrega(String email, EnderecoDTO dto) {
        // Busca o cliente pelo email
        Cliente cliente = clienteRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        // Se o novo endereço for marcado como "padrão", marca os endereços antigos como não padrão
        if (dto.isPadrao()) {
            cliente.getEnderecosEntrega().forEach(e -> e.setPadrao(false));
        }

        // Cria um novo endereço de entrega
        EnderecoEntrega novoEndereco = new EnderecoEntrega();
        novoEndereco.setCep(dto.getCep());
        novoEndereco.setLogradouro(dto.getLogradouro());
        novoEndereco.setNumero(dto.getNumero());
        novoEndereco.setComplemento(dto.getComplemento());
        novoEndereco.setBairro(dto.getBairro());
        novoEndereco.setCidade(dto.getCidade());
        novoEndereco.setUf(dto.getUf());
        novoEndereco.setPadrao(dto.isPadrao());

        // Associa o cliente ao novo endereço
        novoEndereco.setCliente(cliente);

        // Adiciona o novo endereço à lista de endereços de entrega do cliente
        cliente.getEnderecosEntrega().add(novoEndereco);

        // Salva o cliente (isso salvará automaticamente o novo endereço, devido ao relacionamento)
        clienteRepository.save(cliente);
    }


    public Cliente getClienteByEmail(String email) {
        return clienteRepository.findByEmail(email).orElse(null);
    }
}
