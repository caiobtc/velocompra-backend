package com.velocompra.ecommerce.controller;

import com.velocompra.ecommerce.dto.PedidoDTO;
import com.velocompra.ecommerce.dto.PedidoDetalhadoDTO;
import com.velocompra.ecommerce.dto.PedidoResumoDTO;
import com.velocompra.ecommerce.model.Cliente;
import com.velocompra.ecommerce.model.Pedido;
import com.velocompra.ecommerce.service.ClienteService;
import com.velocompra.ecommerce.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador responsável pelas operações relacionadas aos pedidos.
 * Este controlador oferece endpoints para criação, listagem e detalhes de pedidos de clientes.
 */
@RestController
@RequestMapping("api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ClienteService clienteService;

    /**
     * Cria um novo pedido para o cliente autenticado.
     * O pedido é criado com base nas informações fornecidas no DTO e no e-mail do cliente autenticado.
     *
     * @param pedidoDTO O DTO contendo os dados do pedido a ser criado.
     * @return Uma resposta com o número do pedido e o valor total.
     */
    @PostMapping
    public ResponseEntity<?> criarPedido(@RequestBody PedidoDTO pedidoDTO) {
        // Obtém o e-mail do cliente autenticado
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Pedido pedido = pedidoService.criarPedido(pedidoDTO, email);

        Map<String, Object> resposta = new HashMap<>();
        resposta.put("numeroPedido", pedido.getNumeroPedido());
        resposta.put("valorTotal", pedido.getValorTotal());

        return ResponseEntity.ok(resposta); // Retorna o número do pedido e valor total
    }

    /**
     * Lista todos os pedidos do cliente autenticado.
     *
     * @return Uma resposta com a lista de resumos dos pedidos do cliente.
     */
    @GetMapping("/meus-pedidos")
    public ResponseEntity<?> listarPedidosDoCliente() {
        // Obtém o e-mail do cliente autenticado
        String email = getAuthenticatedUserEmail();
        Cliente cliente = clienteService.getClienteByEmail(email);

        List<PedidoResumoDTO> pedidos = pedidoService.listarPedidosDoCliente(cliente.getId());
        return ResponseEntity.ok(pedidos); // Retorna a lista de pedidos do cliente
    }

    /**
     * Recupera os detalhes de um pedido específico, baseado no número do pedido.
     *
     * @param numeroPedido O número do pedido cujos detalhes serão buscados.
     * @return Uma resposta com os detalhes completos do pedido.
     */
    @GetMapping("/{numeroPedido}")
    public ResponseEntity<?> getDetalhesPedido(@PathVariable String numeroPedido) {
        // Obtém o e-mail do cliente autenticado
        String email = getAuthenticatedUserEmail();
        try {
            PedidoDetalhadoDTO pedidoDetalhadoDTO = pedidoService.buscarDetalhesPedido(numeroPedido, email);
            return ResponseEntity.ok(pedidoDetalhadoDTO); // Retorna os detalhes do pedido
        } catch (RuntimeException e) {
            // Adapta a resposta HTTP baseada na mensagem da exceção
            if (e.getMessage().equals("Pedido não encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getMessage().equals("Acesso negado ao pedido")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
            }
            // Para qualquer outra RuntimeException não esperada, retorna 500 Internal Server Error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno do servidor: " + e.getMessage());
        }
    }

    /**
     * Obtém o e-mail do usuário autenticado a partir do contexto de segurança.
     *
     * @return O e-mail do usuário autenticado, ou null se não estiver autenticado.
     */
    private String getAuthenticatedUserEmail() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return (authentication != null && authentication.isAuthenticated())
                ? authentication.getName() // Retorna o nome (e-mail) do usuário autenticado
                : null; // Retorna null se o usuário não estiver autenticado
    }
}
