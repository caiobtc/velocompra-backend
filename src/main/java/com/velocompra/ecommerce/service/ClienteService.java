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

import java.time.LocalDate;
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
        Endereco viaCep = viaCepUtil.buscarCep(endDTO.getCep());

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
        List<Endereco> enderecosEntrega = dto.getEnderecosEntrega().stream().map(endDTOEntrega -> {
            Endereco endereco = viaCepUtil.buscarCep(endDTOEntrega.getCep());
            endereco.setNumero(endDTOEntrega.getNumero());
            endereco.setComplemento(endDTOEntrega.getComplemento());
            return endereco;
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
            Endereco viaCep = viaCepUtil.buscarCep(endDTO.getCep());

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
        Cliente cliente = clienteRepository.findByEmail(email)
                .orElseThrow(()->new RuntimeException("Cliente não encontrado"));

        // se for for marcado como padrão, remover o "padrão" dos anteriores
        if (dto.isPadrao()) {
            cliente.getEnderecosEntrega().forEach(e -> e.setPadrao(false));
        }

        Endereco novo = viaCepUtil.buscarCep(dto.getCep());
        novo.setNumero(dto.getNumero());
        novo.setComplemento(dto.getComplemento());
        novo.setPadrao(dto.isPadrao());

        cliente.getEnderecosEntrega().add(novo);
        clienteRepository.save(cliente);
    }
}
