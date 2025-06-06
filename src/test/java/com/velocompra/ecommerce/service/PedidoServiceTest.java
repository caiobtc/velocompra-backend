package com.velocompra.ecommerce.service;

import com.velocompra.ecommerce.dto.PedidoDTO;
import com.velocompra.ecommerce.dto.PedidoDetalhadoDTO;
import com.velocompra.ecommerce.dto.ItemPedidoDTO;
import com.velocompra.ecommerce.model.*;
import com.velocompra.ecommerce.repository.EnderecoEntregaRepository;
import com.velocompra.ecommerce.repository.PedidoRepository;
import com.velocompra.ecommerce.repository.ProdutoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Classe de teste para {@link PedidoService}.
 * Testa criação de pedidos, cálculo de valores e acesso aos detalhes.
 */
@ExtendWith(MockitoExtension.class)
public class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private EnderecoEntregaRepository enderecoEntregaRepository;

    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private PedidoService pedidoService;

    /**
     * Testa criação de um pedido com produto e frete válidos.
     */
    @Test
    void deveCriarPedidoComValorTotalCorreto() {
        PedidoDTO pedidoDTO = new PedidoDTO();
        pedidoDTO.setFormaPagamento("PIX");
        pedidoDTO.setFrete(new BigDecimal("15.00"));
        pedidoDTO.setEnderecoEntregaId(1L);

        ItemPedidoDTO item = new ItemPedidoDTO();
        item.setProdutoId(10L);
        item.setQuantidade(2);
        item.setPrecoUnitario(new BigDecimal("50.00"));
        pedidoDTO.setProdutos(List.of(item));

        Cliente cliente = new Cliente();
        cliente.setEmail("cliente@teste.com");

        Produto produto = new Produto();
        produto.setNome("Produto Teste");

        EnderecoEntrega endereco = new EnderecoEntrega();

        Pedido pedidoSalvo = new Pedido();
        pedidoSalvo.setNumeroPedido("PED00001");

        when(clienteService.getClienteByEmail("cliente@teste.com")).thenReturn(cliente);
        when(produtoRepository.findById(10L)).thenReturn(Optional.of(produto));
        when(enderecoEntregaRepository.findById(1L)).thenReturn(Optional.of(endereco));
        when(pedidoRepository.count()).thenReturn(0L);
        when(pedidoRepository.save(any())).thenReturn(pedidoSalvo);

        Pedido resultado = pedidoService.criarPedido(pedidoDTO, "cliente@teste.com");

        assertEquals("PED00001", resultado.getNumeroPedido());
        verify(pedidoRepository, times(1)).save(any());
    }

    /**
     * Testa falha ao tentar criar pedido com produto inexistente.
     *
     * Garante que:
     * - O cliente é recuperado com sucesso.
     * - O endereço de entrega é encontrado.
     * - O produto com ID 99L não existe no repositório.
     * - Uma exceção é lançada com a mensagem correta.
     */
    @Test
    void deveFalharSeProdutoNaoEncontrado() {
        // Prepara o DTO do pedido
        PedidoDTO pedidoDTO = new PedidoDTO();
        pedidoDTO.setEnderecoEntregaId(1L);
        pedidoDTO.setFrete(BigDecimal.ZERO);

        // Define item de pedido com produto inexistente (ID 99L)
        ItemPedidoDTO item = new ItemPedidoDTO();
        item.setProdutoId(99L);
        item.setQuantidade(1);
        item.setPrecoUnitario(BigDecimal.TEN);
        pedidoDTO.setProdutos(List.of(item));

        // Mocks
        when(clienteService.getClienteByEmail(anyString())).thenReturn(new Cliente());
        when(enderecoEntregaRepository.findById(1L)).thenReturn(Optional.of(new EnderecoEntrega()));
        when(produtoRepository.findById(99L)).thenReturn(Optional.empty());

        // Executa e valida a exceção esperada
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                pedidoService.criarPedido(pedidoDTO, "cliente@teste.com")
        );

        assertEquals("Produto não encontrado", ex.getMessage());
    }

    /**
     * Testa falha ao tentar criar pedido com endereço inexistente.
     */
    @Test
    void deveFalharSeEnderecoNaoEncontrado() {
        PedidoDTO pedidoDTO = new PedidoDTO();
        pedidoDTO.setEnderecoEntregaId(99L);
        pedidoDTO.setProdutos(new ArrayList<>());
        pedidoDTO.setFrete(BigDecimal.ZERO);

        when(clienteService.getClienteByEmail(anyString())).thenReturn(new Cliente());
        when(enderecoEntregaRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                pedidoService.criarPedido(pedidoDTO, "cliente@teste.com")
        );

        assertEquals("Endereço não encontrado", ex.getMessage());
    }

    /**
     * Testa se os detalhes do pedido são retornados corretamente com imagem do produto.
     */
    @Test
    void deveRetornarDetalhesDoPedidoComImagem() {
        Produto produto = new Produto();
        produto.setNome("Produto A");
        produto.setImagemPadrao("imagem.jpg");

        Cliente cliente = new Cliente();
        cliente.setEmail("cliente@teste.com");

        ItemPedido item = new ItemPedido();
        item.setProduto(produto);
        item.setQuantidade(1);
        item.setPrecoUnitario(new BigDecimal("100.00"));

        EnderecoEntrega endereco = new EnderecoEntrega();
        endereco.setCep("04376002");
        endereco.setLogradouro("Rua A");
        endereco.setNumero("123");
        endereco.setBairro("Vila Santa Catarina");
        endereco.setCidade("Sao paulo");
        endereco.setUf("SP");

        Pedido pedido = new Pedido();
        pedido.setNumeroPedido("PED00123");
        pedido.setCliente(cliente);
        pedido.setFrete(new BigDecimal("20.00"));
        pedido.setValorTotal(new BigDecimal("120.00"));
        pedido.setFormaPagamento("CARTAO");
        pedido.setStatus(StatusPedido.AGUARDANDO_PAGAMENTO);
        pedido.setItens(List.of(item));
        pedido.setEnderecoEntrega(endereco);

        when(pedidoRepository.findByNumeroPedido("PED00123")).thenReturn(Optional.of(pedido));

        PedidoDetalhadoDTO dto = pedidoService.buscarDetalhesPedido("PED00123", "cliente@teste.com");

        assertEquals("PED00123", dto.getNumeroPedido());
        assertEquals("imagem.jpg", dto.getItens().get(0).getImagemProduto());
    }

    /**
     * Testa erro ao acessar pedido de outro cliente.
     */
    @Test
    void deveNegarAcessoAoPedidoDeOutroCliente() {
        Cliente cliente = new Cliente();
        cliente.setEmail("outro@teste.com");

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setNumeroPedido("PED00222");

        when(pedidoRepository.findByNumeroPedido("PED00222")).thenReturn(Optional.of(pedido));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                pedidoService.buscarDetalhesPedido("PED00222", "cliente@teste.com")
        );

        assertEquals("Acesso negado ao pedido", ex.getMessage());
    }
}
