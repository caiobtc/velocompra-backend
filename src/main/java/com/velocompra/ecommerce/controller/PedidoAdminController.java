package com.velocompra.ecommerce.controller;

import com.velocompra.ecommerce.dto.AtualizarStatusPedidoDTO;
import com.velocompra.ecommerce.dto.PedidoResumoDTO;
import com.velocompra.ecommerce.model.Pedido;
import com.velocompra.ecommerce.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/admin/pedidos")
public class PedidoAdminController {

    @Autowired
    private PedidoRepository pedidoRepository;

    @GetMapping
    public ResponseEntity<List<PedidoResumoDTO>> listarTodosPedidos() {
        List<Pedido> pedidos = pedidoRepository.findAllByOrderByDataCriacaoDesc();
        List<PedidoResumoDTO> lista = pedidos.stream()
                .map(PedidoResumoDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(lista);
    }

    @PatchMapping("/{numeroPedido}/status")
    public ResponseEntity<Void> atualizarStatusPedido(@PathVariable String numeroPedido, @RequestBody AtualizarStatusPedidoDTO dto) {
        pedidoRepository.findByNumeroPedido(numeroPedido).ifPresentOrElse(pedido -> {
            pedido.setStatus(dto.getNovoStatusPedido());
            pedidoRepository.save(pedido);
        }, () -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado");
        });

        return ResponseEntity.noContent().build();
    }

}
