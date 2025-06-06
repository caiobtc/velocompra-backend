package com.velocompra.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.velocompra.ecommerce.dto.ItemPedidoDTO;
import com.velocompra.ecommerce.dto.PedidoDTO;
import com.velocompra.ecommerce.dto.PedidoDetalhadoDTO;
import com.velocompra.ecommerce.dto.PedidoResumoDTO;
import com.velocompra.ecommerce.model.Cliente;
import com.velocompra.ecommerce.model.Pedido;
import com.velocompra.ecommerce.model.StatusPedido;
import com.velocompra.ecommerce.service.ClienteService;
import com.velocompra.ecommerce.service.PedidoService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Classe de testes para {@link PedidoController}.
 * Utiliza JUnit 5 para os testes e Mockito para simular dependências.
 */
@ExtendWith(MockitoExtension.class)
class PedidoControllerTest {

    @Mock
    private PedidoService pedidoService;

    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private PedidoController pedidoController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    // Email de exemplo para o cliente autenticado
    private final String CLIENTE_EMAIL = "cliente@example.com";

    @BeforeEach
    void setUp() {
        // Configura o MockMvc com o controlador que está sendo testado
        mockMvc = MockMvcBuilders.standaloneSetup(pedidoController)
                // Se você tiver um GlobalExceptionHandler para tratar RuntimeException, pode adicioná-lo aqui:
                // .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();

        // Configura o SecurityContextHolder para simular um usuário autenticado
        Authentication authentication = new UsernamePasswordAuthenticationToken(CLIENTE_EMAIL, "senha", Collections.emptyList());
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        // Limpa o SecurityContextHolder após cada teste para evitar interferências
        SecurityContextHolder.clearContext();
    }

    /**
     * Testa a criação de um novo pedido com sucesso.
     * Espera que o controlador retorne um status 200 OK e os detalhes do pedido criado.
     *
     * @throws Exception se ocorrer um erro durante a simulação da requisição.
     */
    @Test
    @DisplayName("Deve criar um novo pedido com sucesso")
    void deveCriarPedidoComSucesso() throws Exception {
        // Cenário: Criação de um DTO de pedido
        PedidoDTO pedidoDTO = new PedidoDTO();
        pedidoDTO.setEnderecoEntregaId(1L);
        pedidoDTO.setFormaPagamento("PIX");
        ItemPedidoDTO item1 = new ItemPedidoDTO();
        item1.setProdutoId(10L);
        item1.setQuantidade(2);
        ItemPedidoDTO item2 = new ItemPedidoDTO();
        item2.setProdutoId(20L);
        item2.setQuantidade(1);
        pedidoDTO.setProdutos(Arrays.asList(item1, item2));

        // Cenário: Pedido de retorno do serviço
        Pedido pedidoCriado = new Pedido();
        pedidoCriado.setNumeroPedido("PEDXYZ123");
        pedidoCriado.setValorTotal(new BigDecimal("350.75"));

        // Comportamento esperado do mock: pedidoService.criarPedido() retorna o pedido criado
        when(pedidoService.criarPedido(any(PedidoDTO.class), eq(CLIENTE_EMAIL))).thenReturn(pedidoCriado);

        // Ação: Realiza uma requisição POST para o endpoint de criação de pedido
        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedidoDTO)))
                // Verificação:
                .andExpect(status().isOk()) // Espera um status HTTP 200 OK
                .andExpect(jsonPath("$.numeroPedido", is("PEDXYZ123"))) // Verifica o número do pedido
                .andExpect(jsonPath("$.valorTotal", is(350.75))); // Verifica o valor total

        // Verifica se pedidoService.criarPedido foi chamado uma vez com os argumentos corretos
        verify(pedidoService, times(1)).criarPedido(any(PedidoDTO.class), eq(CLIENTE_EMAIL));
    }

    /**
     * Testa a listagem de pedidos do cliente autenticado.
     * Espera que o controlador retorne um status 200 OK e uma lista de PedidoResumoDTO.
     *
     * @throws Exception se ocorrer um erro durante a simulação da requisição.
     */
    @Test
    @DisplayName("Deve listar os pedidos do cliente autenticado com sucesso")
    void deveListarPedidosDoClienteComSucesso() throws Exception {
        // Cenário: Cliente e lista de pedidos de resumo
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setEmail(CLIENTE_EMAIL);

        PedidoResumoDTO resumo1 = new PedidoResumoDTO();
        resumo1.setNumeroPedido("P001");
        resumo1.setValorTotal(new BigDecimal("100.00"));
        resumo1.setStatus(StatusPedido.CONCLUIDO);

        PedidoResumoDTO resumo2 = new PedidoResumoDTO();
        resumo2.setNumeroPedido("P002");
        resumo2.setValorTotal(new BigDecimal("200.00"));
        resumo2.setStatus(StatusPedido.AGUARDANDO_PAGAMENTO);

        List<PedidoResumoDTO> listaPedidosResumo = Arrays.asList(resumo1, resumo2);

        // Comportamento esperado dos mocks
        when(clienteService.getClienteByEmail(CLIENTE_EMAIL)).thenReturn(cliente);
        when(pedidoService.listarPedidosDoCliente(cliente.getId())).thenReturn(listaPedidosResumo);

        // Ação: Realiza uma requisição GET para o endpoint de listar pedidos do cliente
        mockMvc.perform(get("/api/pedidos/meus-pedidos")
                        .contentType(MediaType.APPLICATION_JSON))
                // Verificação:
                .andExpect(status().isOk()) // Espera um status HTTP 200 OK
                .andExpect(jsonPath("$", hasSize(2))) // Verifica se a lista contém 2 itens
                .andExpect(jsonPath("$[0].numeroPedido", is("P001")))
                .andExpect(jsonPath("$[1].numeroPedido", is("P002")));

        // Verifica se os serviços foram chamados
        verify(clienteService, times(1)).getClienteByEmail(CLIENTE_EMAIL);
        verify(pedidoService, times(1)).listarPedidosDoCliente(cliente.getId());
    }

    /**
     * Testa a recuperação dos detalhes de um pedido específico.
     * Espera que o controlador retorne um status 200 OK e os detalhes completos do pedido.
     *
     * @throws Exception se ocorrer um erro durante a simulação da requisição.
     */
    @Test
    @DisplayName("Deve retornar os detalhes de um pedido específico com sucesso")
    void deveRetornarDetalhesPedidoComSucesso() throws Exception {
        // Cenário: Número do pedido e DTO de detalhes do pedido
        String numeroPedido = "PEDABC";
        PedidoDetalhadoDTO detalhesDTO = new PedidoDetalhadoDTO();
        detalhesDTO.setNumeroPedido(numeroPedido);
        detalhesDTO.setValorTotal(new BigDecimal("500.00"));
        detalhesDTO.setStatus(StatusPedido.ENTREGUE);
        // Pode adicionar mais detalhes se necessário, como itens, endereço, etc.

        // Comportamento esperado do mock: pedidoService.buscarDetalhesPedido() retorna os detalhes
        when(pedidoService.buscarDetalhesPedido(eq(numeroPedido), eq(CLIENTE_EMAIL))).thenReturn(detalhesDTO);

        // Ação: Realiza uma requisição GET para o endpoint de detalhes do pedido
        mockMvc.perform(get("/api/pedidos/{numeroPedido}", numeroPedido)
                        .contentType(MediaType.APPLICATION_JSON))
                // Verificação:
                .andExpect(status().isOk()) // Espera um status HTTP 200 OK
                .andExpect(jsonPath("$.numeroPedido", is(numeroPedido))) // Verifica o número do pedido
                .andExpect(jsonPath("$.valorTotal", is(500.00))) // Verifica o valor total
                .andExpect(jsonPath("$.status", is(StatusPedido.ENTREGUE.name()))); // Verifica o status

        // Verifica se pedidoService.buscarDetalhesPedido foi chamado uma vez
        verify(pedidoService, times(1)).buscarDetalhesPedido(eq(numeroPedido), eq(CLIENTE_EMAIL));
    }

    /**
     * Testa o cenário onde o cliente não possui pedidos.
     * Espera que o controlador retorne um status 200 OK e uma lista vazia.
     *
     * @throws Exception se ocorrer um erro durante a simulação da requisição.
     */
    @Test
    @DisplayName("Deve retornar uma lista vazia quando o cliente não tem pedidos")
    void deveRetornarListaVaziaQuandoClienteNaoTemPedidos() throws Exception {
        // Cenário: Cliente sem pedidos
        Cliente cliente = new Cliente();
        cliente.setId(2L);
        cliente.setEmail(CLIENTE_EMAIL);

        // Comportamento esperado dos mocks
        when(clienteService.getClienteByEmail(CLIENTE_EMAIL)).thenReturn(cliente);
        when(pedidoService.listarPedidosDoCliente(cliente.getId())).thenReturn(Collections.emptyList());

        // Ação: Realiza uma requisição GET
        mockMvc.perform(get("/api/pedidos/meus-pedidos")
                        .contentType(MediaType.APPLICATION_JSON))
                // Verificação:
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(clienteService, times(1)).getClienteByEmail(CLIENTE_EMAIL);
        verify(pedidoService, times(1)).listarPedidosDoCliente(cliente.getId());
    }

    /**
     * Testa o cenário de falha ao buscar detalhes de um pedido inexistente (ou não pertencente ao cliente).
     * O serviço deve lançar uma exceção que o controller deve tratar.
     *
     * @throws Exception se ocorrer um erro durante a simulação da requisição.
     */
    @Test
    @DisplayName("Deve retornar erro ao buscar detalhes de pedido não encontrado ou não pertencente ao cliente")
    void deveRetornarErroAoBuscarDetalhesDePedidoInexistente() throws Exception {
        // Cenário: Pedido não encontrado ou não associado ao cliente autenticado
        String numeroPedidoInexistente = "PEDNONEXIST";

        // Comportamento esperado do mock: pedidoService.buscarDetalhesPedido() lança uma RuntimeException
        when(pedidoService.buscarDetalhesPedido(eq(numeroPedidoInexistente), eq(CLIENTE_EMAIL)))
                .thenThrow(new RuntimeException("Acesso negado ao pedido"));

        // Ação: Realiza uma requisição GET
        mockMvc.perform(get("/api/pedidos/{numeroPedido}", numeroPedidoInexistente)
                        .contentType(MediaType.APPLICATION_JSON))
                // Verificação: Espera um status HTTP 403 Forbidden
                .andExpect(status().isForbidden()) // Spring default for uncaught RuntimeException
                .andExpect(content().string(containsString("Acesso negado ao pedido"))); // Verifica a mensagem no corpo

        // Verifica se o serviço foi chamado
        verify(pedidoService, times(1)).buscarDetalhesPedido(eq(numeroPedidoInexistente), eq(CLIENTE_EMAIL));
    }
}