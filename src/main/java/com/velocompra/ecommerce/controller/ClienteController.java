package com.velocompra.ecommerce.controller;

import com.velocompra.ecommerce.dto.ClienteCadastroDTO;
import com.velocompra.ecommerce.dto.ClienteDTO;
import com.velocompra.ecommerce.dto.ClienteEditarDTO;
import com.velocompra.ecommerce.dto.EnderecoDTO;
import com.velocompra.ecommerce.model.Cliente;
import com.velocompra.ecommerce.model.Endereco;
import com.velocompra.ecommerce.repository.ClienteRepository;
import com.velocompra.ecommerce.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "http://localhost:3000")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ClienteRepository clienteRepository;

    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrarCliente(@RequestBody @Valid ClienteCadastroDTO dto) {
        clienteService.cadastrar(dto);
        return ResponseEntity.ok("Cliente cadastrado com sucesso!");
    }

    @GetMapping("/meus-dados")
    public ResponseEntity<?> getMeusDados(Principal principal) {
        String email = principal.getName();
        Cliente cliente = clienteRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        ClienteDTO dto = new ClienteDTO();
        dto.setNomeCompleto(cliente.getNomeCompleto());
        dto.setEmail(cliente.getEmail());
        dto.setCpf(cliente.getCpf());
        dto.setDataNascimento(cliente.getDataNascimento());
        dto.setGenero(cliente.getGenero());

        // Preencher endereço de faturamento
        if (cliente.getEnderecoFaturamento() != null) {
            EnderecoDTO endFaturamento = new EnderecoDTO();
            endFaturamento.setCep(cliente.getEnderecoFaturamento().getCep());
            endFaturamento.setLogradouro(cliente.getEnderecoFaturamento().getLogradouro());
            endFaturamento.setNumero(cliente.getEnderecoFaturamento().getNumero());
            endFaturamento.setComplemento(cliente.getEnderecoFaturamento().getComplemento());
            endFaturamento.setBairro(cliente.getEnderecoFaturamento().getBairro());
            endFaturamento.setCidade(cliente.getEnderecoFaturamento().getCidade());
            endFaturamento.setUf(cliente.getEnderecoFaturamento().getUf());
            dto.setEnderecoFaturamento(endFaturamento);
        }

        // Preencher endereços de entrega
        if (cliente.getEnderecosEntrega() != null) {
            List<EnderecoDTO> enderecosEntrega = cliente.getEnderecosEntrega().stream().map(endereco -> {
                EnderecoDTO dtoEndereco = new EnderecoDTO();
                dtoEndereco.setCep(endereco.getCep());
                dtoEndereco.setLogradouro(endereco.getLogradouro());
                dtoEndereco.setNumero(endereco.getNumero());
                dtoEndereco.setComplemento(endereco.getComplemento());
                dtoEndereco.setBairro(endereco.getBairro());
                dtoEndereco.setCidade(endereco.getCidade());
                dtoEndereco.setUf(endereco.getUf());
                return dtoEndereco;
            }).toList();
            dto.setEnderecosEntrega(enderecosEntrega);
        }

        return ResponseEntity.ok(dto);
    }

    @PutMapping("/meus-dados")
    public ResponseEntity<?> atualizarMeusDados(@RequestBody @Valid ClienteEditarDTO dto, Principal principal) {
        String email = principal.getName();
        clienteService.atualizarDados(email, dto);
        return ResponseEntity.ok("Dados atualizados com sucesso!");
    }

    @PostMapping("/enderecos-entrega")
    public ResponseEntity<?> adicionarEnderecoEntrega(@RequestBody EnderecoDTO novoEndereco, Principal principal) {
        clienteService.adicionarEnderecoEntrega(principal.getName(), novoEndereco);
        return ResponseEntity.ok().build();
    }
}
