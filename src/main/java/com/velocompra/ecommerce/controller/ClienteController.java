package com.velocompra.ecommerce.controller;

import com.velocompra.ecommerce.dto.ClienteCadastroDTO;
import com.velocompra.ecommerce.dto.ClienteDTO;
import com.velocompra.ecommerce.dto.ClienteEditarDTO;
import com.velocompra.ecommerce.dto.EnderecoDTO;
import com.velocompra.ecommerce.model.Cliente;
import com.velocompra.ecommerce.model.EnderecoEntrega;
import com.velocompra.ecommerce.repository.ClienteRepository;
import com.velocompra.ecommerce.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * Controlador responsável pelas operações relacionadas ao cliente.
 * Este controlador oferece endpoints para cadastro, edição de dados, consulta e atualização de endereços de entrega.
 */
@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "http://localhost:3000")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ClienteRepository clienteRepository;

    /**
     * Realiza o cadastro de um novo cliente no sistema.
     *
     * @param dto O DTO contendo os dados do cliente a ser cadastrado.
     * @return Uma resposta indicando o sucesso do cadastro.
     */
    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrarCliente(@RequestBody @Valid ClienteCadastroDTO dto) {
        clienteService.cadastrar(dto);
        return ResponseEntity.ok("Cliente cadastrado com sucesso!");
    }

    /**
     * Recupera os dados do cliente autenticado.
     * Este endpoint retorna as informações do cliente, incluindo nome, e-mail, CPF, dados pessoais,
     * endereço de faturamento e endereços de entrega.
     *
     * @param principal O principal do usuário autenticado, utilizado para recuperar o e-mail.
     * @return Uma resposta com os dados do cliente.
     */
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

    /**
     * Atualiza os dados do cliente autenticado.
     * Permite a alteração de informações pessoais, como nome, data de nascimento, gênero,
     * e também a atualização de endereço de faturamento e senha.
     *
     * @param dto O DTO contendo os dados a serem atualizados.
     * @param principal O principal do usuário autenticado, utilizado para recuperar o e-mail.
     * @return Uma resposta indicando o sucesso da atualização dos dados.
     */
    @PutMapping("/meus-dados")
    public ResponseEntity<?> atualizarMeusDados(@RequestBody @Valid ClienteEditarDTO dto, Principal principal) {
        String email = principal.getName();
        clienteService.atualizarDados(email, dto);
        return ResponseEntity.ok("Dados atualizados com sucesso!");
    }

    /**
     * Adiciona um novo endereço de entrega para o cliente autenticado.
     * Verifica a validade do CEP e chama o serviço para persistir o novo endereço de entrega.
     *
     * @param novoEndereco O DTO contendo os dados do novo endereço de entrega.
     * @param principal O principal do usuário autenticado, utilizado para recuperar o e-mail.
     * @return Uma resposta de sucesso ou erro, caso o CEP seja inválido ou algum outro erro ocorra.
     */
    @PostMapping("/enderecos-entrega")
    @PreAuthorize("hasAuthority('CLIENTE')")
    public ResponseEntity<?> adicionarEnderecoEntrega(@RequestBody @Valid EnderecoDTO novoEndereco, Principal principal) {
        // Log para debug, imprimindo o novo endereço recebido
        System.out.println("Novo endereço recebido: " + novoEndereco);

        // Verificação para garantir que o CEP seja válido
        if (novoEndereco.getCep() == null || novoEndereco.getCep().length() != 8) {
            return ResponseEntity.badRequest().body("CEP inválido.");
        }

        // Chama o serviço para adicionar o novo endereço
        clienteService.adicionarEnderecoEntrega(principal.getName(), novoEndereco);

        // Retorna uma resposta de sucesso
        return ResponseEntity.ok().build();
    }

}
