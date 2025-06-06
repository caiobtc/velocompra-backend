package com.velocompra.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.velocompra.ecommerce.dto.AtualizarStatusPedidoDTO;
import com.velocompra.ecommerce.dto.PedidoResumoDTO;
import com.velocompra.ecommerce.exception.GlobalExceptionHandler;
import com.velocompra.ecommerce.model.Pedido;
import com.velocompra.ecommerce.model.StatusPedido;
import com.velocompra.ecommerce.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PedidoAdminControllerTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private PedidoAdminController pedidoAdminController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Configura o MockMvc com o controlador que está sendo testado
        mockMvc = MockMvcBuilders.standaloneSetup(pedidoAdminController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    /**
     * Testa o cenário de sucesso ao listar todos os pedidos quando há pedidos cadastrados.
     * Espera que o controlador retorne um status 200 OK e uma lista de PedidoResumoDTO.
     *
     * @throws Exception se ocorrer um erro durante a simulação da requisição.
     */
    @Test
    @DisplayName("Deve listar todos os pedidos com sucesso quando há pedidos")
    void deveListarTodosPedidosComSucessoQuandoHaPedidos() throws Exception {
        // Cenário: Criação de pedidos de exemplo
        Pedido pedido1 = new Pedido();
        pedido1.setId(1L);
        pedido1.setNumeroPedido("PED001");
        pedido1.setDataCriacao(LocalDateTime.now().minusHours(2));
        pedido1.setValorTotal(new BigDecimal("100.00"));
        pedido1.setStatus(StatusPedido.AGUARDANDO_PAGAMENTO);
        // Não é necessário mockar cliente ou itens aqui, pois PedidoResumoDTO não os utiliza diretamente
        // e o mock do repository já é suficiente.

        Pedido pedido2 = new Pedido();
        pedido2.setId(2L);
        pedido2.setNumeroPedido("PED002");
        pedido2.setDataCriacao(LocalDateTime.now().minusHours(1));
        pedido2.setValorTotal(new BigDecimal("250.00"));
        pedido2.setStatus(StatusPedido.EM_PROCESSAMENTO);

        List<Pedido> pedidos = Arrays.asList(pedido1, pedido2);

        // Comportamento esperado do mock: pedidoRepository.findAllByOrderByDataCriacaoDesc() retorna a lista de pedidos.
        when(pedidoRepository.findAllByOrderByDataCriacaoDesc()).thenReturn(pedidos);

        // Ação: Realiza uma requisição GET para o endpoint de listar todos os pedidos
        mockMvc.perform(get("/api/admin/pedidos")
                        .contentType(MediaType.APPLICATION_JSON))
                // Verificação:
                .andExpect(status().isOk()) // Espera um status HTTP 200 OK
                .andExpect(jsonPath("$", hasSize(2))) // Verifica se a lista contém 2 itens
                .andExpect(jsonPath("$[0].numeroPedido", is(pedido1.getNumeroPedido())))
                .andExpect(jsonPath("$[0].valorTotal", is(100.00)))
                .andExpect(jsonPath("$[0].status", is(pedido1.getStatus().name())))
                .andExpect(jsonPath("$[1].numeroPedido", is(pedido2.getNumeroPedido())))
                .andExpect(jsonPath("$[1].valorTotal", is(250.00)))
                .andExpect(jsonPath("$[1].status", is(pedido2.getStatus().name())));

        // Verifica se pedidoRepository.findAllByOrderByDataCriacaoDesc foi chamado uma vez
        verify(pedidoRepository, times(1)).findAllByOrderByDataCriacaoDesc();
    }

    /**
     * Testa o cenário de sucesso ao listar todos os pedidos quando não há pedidos cadastrados.
     * Espera que o controlador retorne um status 200 OK e uma lista vazia.
     *
     * @throws Exception se ocorrer um erro durante a simulação da requisição.
     */
    @Test
    @DisplayName("Deve listar todos os pedidos com sucesso quando não há pedidos")
    void deveListarTodosPedidosComSucessoQuandoNaoHaPedidos() throws Exception {
        // Cenário: Nenhuma pedido cadastrado
        when(pedidoRepository.findAllByOrderByDataCriacaoDesc()).thenReturn(Collections.emptyList());

        // Ação: Realiza uma requisição GET
        mockMvc.perform(get("/api/admin/pedidos")
                        .contentType(MediaType.APPLICATION_JSON))
                // Verificação:
                .andExpect(status().isOk()) // Espera status 200 OK
                .andExpect(jsonPath("$", hasSize(0))); // Espera uma lista vazia

        // Verifica se o método foi chamado uma vez
        verify(pedidoRepository, times(1)).findAllByOrderByDataCriacaoDesc();
    }

    /**
     * Testa o cenário de sucesso ao atualizar o status de um pedido existente.
     * Espera que o controlador retorne um status 204 No Content.
     *
     * @throws Exception se ocorrer um erro durante a simulação da requisição.
     */
    @Test
    @DisplayName("Deve atualizar o status do pedido com sucesso")
    void deveAtualizarStatusPedidoComSucesso() throws Exception {
        // Cenário: Pedido existente e DTO com novo status
        String numeroPedido = "PED001";
        AtualizarStatusPedidoDTO dto = new AtualizarStatusPedidoDTO();
        dto.setNovoStatusPedido(StatusPedido.ENVIADO);

        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setNumeroPedido(numeroPedido);
        pedido.setStatus(StatusPedido.EM_PROCESSAMENTO); // Status inicial

        // Comportamento esperado do mock: findByNumeroPedido retorna o pedido, save atualiza o pedido.
        when(pedidoRepository.findByNumeroPedido(numeroPedido)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido); // Retorna o pedido salvo

        // Ação: Realiza uma requisição PATCH para o endpoint de atualização de status
        mockMvc.perform(patch("/api/admin/pedidos/{numeroPedido}/status", numeroPedido)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                // Verificação: Espera um status HTTP 204 No Content
                .andExpect(status().isNoContent());

        // Verifica se findByNumeroPedido foi chamado uma vez
        verify(pedidoRepository, times(1)).findByNumeroPedido(numeroPedido);
        // Verifica se o status do pedido foi realmente atualizado antes de salvar
        verify(pedidoRepository, times(1)).save(argThat(p -> p.getStatus() == StatusPedido.ENVIADO));
    }

    /**
     * Testa o cenário de falha ao tentar atualizar o status de um pedido que não existe.
     * Espera que o controlador retorne um status 404 Not Found.
     *
     * @throws Exception se ocorrer um erro durante a simulação da requisição.
     */
    @Test
    @DisplayName("Deve retornar 404 Not Found ao tentar atualizar status de pedido inexistente")
    void deveRetornar404QuandoAtualizarStatusPedidoInexistente() throws Exception {
        // Cenário: Pedido inexistente
        String numeroPedido = "PED999";
        AtualizarStatusPedidoDTO dto = new AtualizarStatusPedidoDTO();
        dto.setNovoStatusPedido(StatusPedido.CANCELADO);

        // Comportamento esperado do mock: findByNumeroPedido retorna Optional.empty()
        when(pedidoRepository.findByNumeroPedido(numeroPedido)).thenReturn(Optional.empty());

        // Ação: Realiza uma requisição PATCH
        mockMvc.perform(patch("/api/admin/pedidos/{numeroPedido}/status", numeroPedido)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                // Verificação: Espera um status HTTP 404 Not Found
                .andExpect(status().isNotFound())
                .andExpect(content().string(is("Pedido não encontrado")));// Verifica a mensagem de erro diretamente no corpo da resposta

        // Verifica se findByNumeroPedido foi chamado uma vez e save nunca foi chamado
        verify(pedidoRepository, times(1)).findByNumeroPedido(numeroPedido);
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }
}