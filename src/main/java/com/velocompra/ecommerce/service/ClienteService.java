package com.velocompra.ecommerce.service;

import com.velocompra.ecommerce.dto.ClienteCadastroDTO;
import com.velocompra.ecommerce.dto.EnderecoDTO;
import com.velocompra.ecommerce.exception.ConflictException;
import com.velocompra.ecommerce.model.Cliente;
import com.velocompra.ecommerce.model.Endereco;
import com.velocompra.ecommerce.repository.ClienteRepository;
import com.velocompra.ecommerce.util.ViaCepClient;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

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

        // Criptografa a senha
        String senhaCriptografada = passwordEncoder.encode(dto.getSenha());

        // Converte data de nascimento de String para LocalDate
        LocalDate dataNascimento = dto.getDataNascimento();

        // Cria cliente
        Cliente cliente = new Cliente();
        cliente.setNomeCompleto(dto.getNome());
        cliente.setEmail(dto.getEmail());
        cliente.setCpf(dto.getCpf());
        cliente.setDataNascimento(dataNascimento);
        cliente.setGenero(dto.getGenero());
        cliente.setSenha(senhaCriptografada);

        // Endereço de faturamento via ViaCEP
        Endereco enderecoFaturamento = viaCepUtil.buscarCep(dto.getEnderecoFaturamento().getCep());
        enderecoFaturamento.setNumero(dto.getEnderecoFaturamento().getNumero());
        enderecoFaturamento.setComplemento(dto.getEnderecoFaturamento().getComplemento());

        cliente.setEnderecoFaturamento(enderecoFaturamento);

        // Mapeia os endereços de entrega
        List<Endereco> enderecosEntrega = dto.getEnderecosEntrega().stream().map(endDTO -> {
            Endereco endereco = viaCepUtil.buscarCep(endDTO.getCep());
            endereco.setNumero(endDTO.getNumero());
            endereco.setComplemento(endDTO.getComplemento());
            return endereco;
        }).collect(Collectors.toList());

        cliente.setEnderecosEntrega(enderecosEntrega);

        clienteRepository.save(cliente);
    }
}
