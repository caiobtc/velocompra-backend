package com.velocompra.ecommerce.controller;

import com.velocompra.ecommerce.dto.PedidoDTO;
import com.velocompra.ecommerce.dto.PedidoDetalhadoDTO;
import com.velocompra.ecommerce.dto.PedidoResumoDTO;
import com.velocompra.ecommerce.model.Cliente;
import com.velocompra.ecommerce.model.Pedido;
import com.velocompra.ecommerce.service.ClienteService;
import com.velocompra.ecommerce.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ClienteService clienteService;

    @PostMapping
    public ResponseEntity<?> criarPedido(@RequestBody PedidoDTO pedidoDTO) {
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Pedido pedido = pedidoService.criarPedido(pedidoDTO, email);

        Map<String, Object> resposta = new HashMap<>();
        resposta.put("numeroPedido", pedido.getNumeroPedido());
        resposta.put("valorTotal", pedido.getValorTotal());

        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/meus-pedidos")
    public ResponseEntity<?> listarPedidosDoCliente() {
        String email = getAuthenticatedUserEmail();
        Cliente cliente = clienteService.getClienteByEmail(email);

        List<PedidoResumoDTO> pedidos = pedidoService.listarPedidosDoCliente(cliente.getId());
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/{numeroPedido}")
    public ResponseEntity<?> getDetalhesPedido(@PathVariable String numeroPedido) {
        String email = getAuthenticatedUserEmail();
        PedidoDetalhadoDTO pedidoDetalhadoDTO = pedidoService.buscarDetalhesPedido(numeroPedido, email);
        return ResponseEntity.ok(pedidoDetalhadoDTO);
    }


    private String getAuthenticatedUserEmail() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return (authentication != null && authentication.isAuthenticated())
                ? authentication.getName()
                : null;
    }
}
