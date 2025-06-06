package com.velocompra.ecommerce.controller;

import com.velocompra.ecommerce.dto.ClienteCadastroDTO;
import com.velocompra.ecommerce.dto.ClienteDTO;
import com.velocompra.ecommerce.dto.ClienteEditarDTO;
import com.velocompra.ecommerce.dto.EnderecoDTO;
import com.velocompra.ecommerce.model.Cliente;
import com.velocompra.ecommerce.model.EnderecoFaturamento;
import com.velocompra.ecommerce.model.EnderecoEntrega;
import com.velocompra.ecommerce.repository.ClienteRepository;
import com.velocompra.ecommerce.service.ClienteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Classe de testes para {@link ClienteController}.
 * Utiliza JUnit 5 para os testes e Mockito para simular dependências.
 */
@ExtendWith(MockitoExtension.class)
class ClienteControllerTest {

    @InjectMocks
    private ClienteController clienteController;

    @Mock
    private ClienteService clienteService;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private Principal principal;

    /**
     * Testa o cadastro de um novo cliente.
     * Deve chamar o serviço de cliente para cadastrar e retornar um status 200 (OK).
     */
    @Test
    @DisplayName("Deve registrar um novo cliente com sucesso")
    void deveCadastrarClienteComSucesso() { // Renomeado de cadastrarCliente_success
        // Cria um DTO de cadastro de cliente
        ClienteCadastroDTO dto = new ClienteCadastroDTO();
        dto.setEmail("novo@email.com");
        dto.setSenha("senha123");

        // Chama o método do controller
        ResponseEntity<?> response = clienteController.cadastrarCliente(dto);

        // Verifica se o serviço de cliente foi chamado para cadastrar
        verify(clienteService, times(1)).cadastrar(dto);
        // Verifica se o status da resposta é 200
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Verifica se o corpo da resposta é a mensagem esperada
        assertEquals("Cliente cadastrado com sucesso!", response.getBody());
    }

    /**
     * Testa a recuperação dos dados do cliente autenticado.
     * Deve retornar um status 200 (OK) com os dados do cliente mapeados para ClienteDTO.
     */
    @Test
    @DisplayName("Deve retornar dados do cliente autenticado com sucesso")
    void deveRetornarMeusDadosComSucesso() { // Renomeado de getMeusDados_success
        // Simula um email autenticado
        String email = "cliente@example.com";
        when(principal.getName()).thenReturn(email);

        // Cria um cliente mock
        Cliente cliente = new Cliente();
        cliente.setNomeCompleto("Cliente Teste");
        cliente.setEmail(email);
        cliente.setCpf("123.456.789-00");
        cliente.setDataNascimento(LocalDate.of(1990, 1, 1));
        cliente.setGenero("Masculino");

        // Adiciona um endereço de faturamento
        EnderecoFaturamento endFaturamento = new EnderecoFaturamento();
        endFaturamento.setCep("11111-111");
        endFaturamento.setLogradouro("Rua Faturamento");
        cliente.setEnderecoFaturamento(endFaturamento);

        // Adiciona endereços de entrega
        EnderecoEntrega endEntrega1 = new EnderecoEntrega();
        endEntrega1.setCep("22222-222");
        endEntrega1.setLogradouro("Rua Entrega 1");
        EnderecoEntrega endEntrega2 = new EnderecoEntrega();
        endEntrega2.setCep("33333-333");
        endEntrega2.setLogradouro("Rua Entrega 2");
        cliente.setEnderecosEntrega(List.of(endEntrega1, endEntrega2));

        // Simula o comportamento do repositório
        when(clienteRepository.findByEmail(email)).thenReturn(Optional.of(cliente));

        // Chama o método do controller
        ResponseEntity<?> response = clienteController.getMeusDados(principal);

        // Verificações
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertThat(response.getBody(), instanceOf(ClienteDTO.class));

        ClienteDTO resultDto = (ClienteDTO) response.getBody();

        // Verifica os dados do cliente
        assertEquals("Cliente Teste", resultDto.getNomeCompleto());
        assertEquals(email, resultDto.getEmail());
        assertEquals("123.456.789-00", resultDto.getCpf());
        assertEquals(LocalDate.of(1990, 1, 1), resultDto.getDataNascimento());
        assertEquals("Masculino", resultDto.getGenero());

        // Verifica o endereço de faturamento
        assertNotNull(resultDto.getEnderecoFaturamento());
        assertEquals("11111-111", resultDto.getEnderecoFaturamento().getCep());
        assertEquals("Rua Faturamento", resultDto.getEnderecoFaturamento().getLogradouro());

        // Verifica os endereços de entrega
        assertNotNull(resultDto.getEnderecosEntrega());
        assertEquals(2, resultDto.getEnderecosEntrega().size());
        assertEquals("22222-222", resultDto.getEnderecosEntrega().get(0).getCep());
        assertEquals("Rua Entrega 1", resultDto.getEnderecosEntrega().get(0).getLogradouro());
        assertEquals("33333-333", resultDto.getEnderecosEntrega().get(1).getCep());
        assertEquals("Rua Entrega 2", resultDto.getEnderecosEntrega().get(1).getLogradouro());
    }

    /**
     * Testa a recuperação dos dados do cliente quando o cliente não é encontrado.
     * Deve lançar uma RuntimeException.
     */
    @Test
    @DisplayName("Deve lançar RuntimeException se o cliente não for encontrado ao obter os dados")
    void deveLancarRuntimeExceptionQuandoClienteNaoEncontradoEmMeusDados() { // Renomeado de getMeusDados_clientNotFound_throwsRuntimeException
        // Simula um email autenticado
        String email = "cliente@example.com";
        when(principal.getName()).thenReturn(email);

        // Simula que o cliente não é encontrado pelo repositório
        when(clienteRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Verifica se uma RuntimeException é lançada
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                clienteController.getMeusDados(principal)
        );

        // Verifica a mensagem da exceção
        assertEquals("Cliente não encontrado", exception.getMessage());
    }

    /**
     * Testa a atualização dos dados do cliente autenticado.
     * Deve chamar o serviço de cliente para atualizar e retornar um status 200 (OK).
     */
    @Test
    @DisplayName("Deve atualizar os dados do cliente autenticado com sucesso")
    void deveAtualizarMeusDadosComSucesso() { // Renomeado de atualizarMeusDados_success
        // Simula um email autenticado
        String email = "cliente@example.com";
        when(principal.getName()).thenReturn(email);

        // Cria um DTO de edição de cliente
        ClienteEditarDTO dto = new ClienteEditarDTO();
        dto.setNome("Novo Nome");

        // Chama o método do controller
        ResponseEntity<?> response = clienteController.atualizarMeusDados(dto, principal);

        // Verifica se o serviço de cliente foi chamado para atualizar os dados
        verify(clienteService, times(1)).atualizarDados(email, dto);
        // Verifica se o status da resposta é 200
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Verifica se o corpo da resposta é a mensagem esperada
        assertEquals("Dados atualizados com sucesso!", response.getBody());
    }

    /**
     * Testa a adição de um novo endereço de entrega com um CEP válido.
     * Deve chamar o serviço para adicionar o endereço e retornar um status 200 (OK) sem conteúdo.
     */
    @Test
    @DisplayName("Deve adicionar um novo endereço de entrega com CEP válido com sucesso")
    void deveAdicionarEnderecoEntregaComCepValidoComSucesso() { // Renomeado de adicionarEnderecoEntrega_validCep_success
        // Simula um email autenticado
        String email = "cliente@example.com";
        when(principal.getName()).thenReturn(email);

        // Cria um DTO de endereço com um CEP válido
        EnderecoDTO novoEndereco = new EnderecoDTO();
        novoEndereco.setCep("12345678"); // CEP válido com 8 dígitos
        novoEndereco.setLogradouro("Rua Nova");

        // Chama o método do controller
        ResponseEntity<?> response = clienteController.adicionarEnderecoEntrega(novoEndereco, principal);

        // Verifica se o serviço de cliente foi chamado para adicionar o endereço
        verify(clienteService, times(1)).adicionarEnderecoEntrega(email, novoEndereco);
        // Verifica se o status da resposta é 200
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Verifica se o corpo da resposta é nulo (ResponseEntity.ok().build() retorna um corpo vazio)
        assertEquals(null, response.getBody());
    }

    /**
     * Testa a adição de um novo endereço de entrega com um CEP inválido (nulo).
     * Deve retornar um status 400 (Bad Request) com a mensagem apropriada.
     */
    @Test
    @DisplayName("Deve retornar 400 se o CEP for nulo ao adicionar endereço de entrega")
    void deveRetornar400QuandoCepForNuloAoAdicionarEnderecoEntrega() { // Renomeado de adicionarEnderecoEntrega_nullCep_returnsBadRequest

        // Cria um DTO de endereço com CEP nulo
        EnderecoDTO novoEndereco = new EnderecoDTO();
        novoEndereco.setLogradouro("Rua Nova");
        novoEndereco.setCep(null); // CEP nulo

        // Chama o método do controller
        ResponseEntity<?> response = clienteController.adicionarEnderecoEntrega(novoEndereco, principal);

        // Verifica se o serviço de cliente não foi chamado para adicionar o endereço
        verify(clienteService, never()).adicionarEnderecoEntrega(anyString(), any(EnderecoDTO.class));
        // Verifica se o status da resposta é 400
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        // Verifica se o corpo da resposta contém a mensagem esperada
        assertEquals("CEP inválido.", response.getBody());
    }

    /**
     * Testa a adição de um novo endereço de entrega com um CEP inválido (comprimento incorreto).
     * Deve retornar um status 400 (Bad Request) com a mensagem apropriada.
     */
    @Test
    @DisplayName("Deve retornar 400 se o CEP tiver comprimento inválido ao adicionar endereço de entrega")
    void deveRetornar400QuandoCepTiverComprimentoInvalidoAoAdicionarEnderecoEntrega() { // Renomeado de adicionarEnderecoEntrega_invalidLengthCep_returnsBadRequest

        // Cria um DTO de endereço com CEP de comprimento inválido
        EnderecoDTO novoEndereco = new EnderecoDTO();
        novoEndereco.setLogradouro("Rua Nova");
        novoEndereco.setCep("123"); // CEP com comprimento inválido

        // Chama o método do controller
        ResponseEntity<?> response = clienteController.adicionarEnderecoEntrega(novoEndereco, principal);

        // Verifica se o serviço de cliente não foi chamado para adicionar o endereço
        verify(clienteService, never()).adicionarEnderecoEntrega(anyString(), any(EnderecoDTO.class));
        // Verifica se o status da resposta é 400
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        // Verifica se o corpo da resposta contém a mensagem esperada
        assertEquals("CEP inválido.", response.getBody());
    }
}