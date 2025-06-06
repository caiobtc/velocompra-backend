package com.velocompra.ecommerce.service;

import com.velocompra.ecommerce.dto.ClienteCadastroDTO;
import com.velocompra.ecommerce.dto.ClienteEditarDTO;
import com.velocompra.ecommerce.dto.EnderecoDTO;
import com.velocompra.ecommerce.exception.ConflictException;
import com.velocompra.ecommerce.model.*;
import com.velocompra.ecommerce.repository.ClienteRepository;
import com.velocompra.ecommerce.repository.EnderecoFaturamentoRepository;
import com.velocompra.ecommerce.util.ViaCepClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para {@link ClienteService}.
 */
@ExtendWith(MockitoExtension.class)
public class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private EnderecoFaturamentoRepository enderecoFaturamentoRepository;

    @Mock
    private ViaCepClient viaCepClient;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private ClienteService clienteService;

    /**
     * Testa o cadastro de cliente com dados válidos.
     */
    @Test
    void deveCadastrarClienteComDadosValidos() {
        ClienteCadastroDTO dto = new ClienteCadastroDTO();
        dto.setNome("Caio Vieira");
        dto.setCpf("12345678900");
        dto.setEmail("caio@email.com");
        dto.setSenha("senha123");
        dto.setDataNascimento(LocalDate.of(1995, 8, 15));

        EnderecoDTO endDTO = new EnderecoDTO();
        endDTO.setCep("01001-000");
        endDTO.setNumero("123");
        endDTO.setComplemento("Apto 1");
        dto.setEnderecoFaturamento(endDTO);

        EnderecoEntrega enderecoViaCep = new EnderecoEntrega();
        enderecoViaCep.setCep("01001-000");
        enderecoViaCep.setLogradouro("Rua A");
        enderecoViaCep.setBairro("Centro");
        enderecoViaCep.setCidade("São Paulo");
        enderecoViaCep.setUf("SP");

        dto.setEnderecosEntrega(List.of(endDTO));

        when(clienteRepository.existsByCpf(dto.getCpf())).thenReturn(false);
        when(clienteRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(viaCepClient.buscarCep("01001-000")).thenReturn(enderecoViaCep);

        clienteService.cadastrar(dto);

        verify(clienteRepository).save(any(Cliente.class));
    }

    /**
     * Testa conflito ao cadastrar cliente com CPF já cadastrado.
     */
    @Test
    void naoDeveCadastrarSeCpfJaExistir() {
        ClienteCadastroDTO dto = new ClienteCadastroDTO();
        dto.setCpf("11111111111");

        when(clienteRepository.existsByCpf("11111111111")).thenReturn(true);

        ConflictException ex = assertThrows(ConflictException.class, () ->
                clienteService.cadastrar(dto));

        assertEquals("CPF já cadastrado!", ex.getMessage());
    }

    /**
     * Testa conflito ao cadastrar cliente com e-mail já cadastrado.
     */
    @Test
    void naoDeveCadastrarSeEmailJaExistir() {
        ClienteCadastroDTO dto = new ClienteCadastroDTO();
        dto.setCpf("22222222222");
        dto.setEmail("existe@email.com");

        when(clienteRepository.existsByCpf(any())).thenReturn(false);
        when(clienteRepository.existsByEmail("existe@email.com")).thenReturn(true);

        ConflictException ex = assertThrows(ConflictException.class, () ->
                clienteService.cadastrar(dto));

        assertEquals("Email já cadastrado!", ex.getMessage());
    }

    /**
     * Testa atualização de dados do cliente com sucesso (sem troca de senha).
     */
    @Test
    void deveAtualizarDadosCliente() {
        String email = "cliente@email.com";

        Cliente cliente = new Cliente();
        cliente.setEmail(email);
        cliente.setSenha(passwordEncoder.encode("senhaAtual"));

        ClienteEditarDTO dto = new ClienteEditarDTO();
        dto.setNome("Novo Nome");
        dto.setDataNascimento(LocalDate.of(2000, 1, 1));

        when(clienteRepository.findByEmail(email)).thenReturn(Optional.of(cliente));

        clienteService.atualizarDados(email, dto);

        assertEquals("Novo Nome", cliente.getNomeCompleto());
        verify(clienteRepository).save(cliente);
    }

    /**
     * Testa falha ao atualizar dados de cliente inexistente.
     */
    @Test
    void naoDeveAtualizarSeClienteNaoExistir() {
        when(clienteRepository.findByEmail("naoexiste@email.com")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                clienteService.atualizarDados("naoexiste@email.com", new ClienteEditarDTO()));

        assertEquals("Cliente não encontrado", ex.getMessage());
    }

    /**
     * Testa falha ao trocar senha com senha atual incorreta.
     */
    @Test
    void naoDeveAtualizarSeSenhaAtualIncorreta() {
        Cliente cliente = new Cliente();
        cliente.setEmail("cliente@email.com");
        cliente.setSenha(passwordEncoder.encode("correta"));

        ClienteEditarDTO dto = new ClienteEditarDTO();
        dto.setSenhaAtual("errada");
        dto.setNovaSenha("nova");

        when(clienteRepository.findByEmail(cliente.getEmail())).thenReturn(Optional.of(cliente));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                clienteService.atualizarDados(cliente.getEmail(), dto));

        assertEquals("Senha atual incorreta", ex.getMessage());
    }

    /**
     * Testa a adição de um novo endereço de entrega marcado como padrão.
     */
    @Test
    void deveAdicionarEnderecoEntregaPadrao() {
        Cliente cliente = new Cliente();
        cliente.setEmail("cliente@teste.com");
        cliente.setEnderecosEntrega(new ArrayList<>());

        EnderecoDTO dto = new EnderecoDTO();
        dto.setCep("01001-000");
        dto.setLogradouro("Rua Teste");
        dto.setNumero("123");
        dto.setComplemento("Apto 2");
        dto.setBairro("Centro");
        dto.setCidade("SP");
        dto.setUf("SP");
        dto.setPadrao(true);

        when(clienteRepository.findByEmail("cliente@teste.com")).thenReturn(Optional.of(cliente));

        clienteService.adicionarEnderecoEntrega("cliente@teste.com", dto);

        assertThat(cliente.getEnderecosEntrega(), hasSize(1));
        assertTrue(cliente.getEnderecosEntrega().get(0).getPadrao());
        verify(clienteRepository).save(cliente);
    }

    /**
     * Testa busca de cliente existente pelo e-mail.
     */
    @Test
    void deveRetornarClientePorEmail() {
        Cliente cliente = new Cliente();
        cliente.setEmail("email@teste.com");

        when(clienteRepository.findByEmail("email@teste.com")).thenReturn(Optional.of(cliente));

        Cliente resultado = clienteService.getClienteByEmail("email@teste.com");

        assertNotNull(resultado);
        assertEquals("email@teste.com", resultado.getEmail());
    }

    /**
     * Testa retorno null ao buscar cliente inexistente por e-mail.
     */
    @Test
    void deveRetornarNullSeClienteNaoExistir() {
        when(clienteRepository.findByEmail("inexistente@email.com")).thenReturn(Optional.empty());

        Cliente resultado = clienteService.getClienteByEmail("inexistente@email.com");

        assertNull(resultado);
    }
}
