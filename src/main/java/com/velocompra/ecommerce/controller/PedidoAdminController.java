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

/**
 * Controlador responsável pelas operações administrativas dos pedidos.
 * Este controlador oferece endpoints para listar todos os pedidos e para atualizar o status de um pedido.
 */
@RestController
@RequestMapping("api/admin/pedidos")
public class PedidoAdminController {

    @Autowired
    private PedidoRepository pedidoRepository;

    /**
     * Lista todos os pedidos do sistema, ordenados pela data de criação (do mais recente para o mais antigo).
     * Retorna um resumo de cada pedido.
     *
     * @return Uma resposta contendo a lista de resumos dos pedidos.
     */
    @GetMapping
    public ResponseEntity<List<PedidoResumoDTO>> listarTodosPedidos() {
        List<Pedido> pedidos = pedidoRepository.findAllByOrderByDataCriacaoDesc();
        List<PedidoResumoDTO> lista = pedidos.stream()
                .map(PedidoResumoDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(lista); // Retorna a lista de pedidos como resposta
    }

    /**
     * Atualiza o status de um pedido específico com base no número do pedido fornecido.
     * Este endpoint é utilizado para alterar o status de um pedido. Se o pedido não for encontrado, um erro 404 é retornado.
     *
     * @param numeroPedido O número do pedido cujo status será alterado.
     * @param dto O DTO contendo o novo status do pedido.
     * @return Uma resposta de sucesso, sem conteúdo.
     * @throws ResponseStatusException Se o pedido não for encontrado, um erro 404 é gerado.
     */
    @PatchMapping("/{numeroPedido}/status")
    public ResponseEntity<Void> atualizarStatusPedido(@PathVariable String numeroPedido, @RequestBody AtualizarStatusPedidoDTO dto) {
        pedidoRepository.findByNumeroPedido(numeroPedido).ifPresentOrElse(pedido -> {
            pedido.setStatus(dto.getNovoStatusPedido());
            pedidoRepository.save(pedido); // Atualiza o status do pedido no banco de dados
        }, () -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado"); // Retorna erro 404 se o pedido não for encontrado
        });

        return ResponseEntity.noContent().build(); // Retorna resposta de sucesso com código 204 (sem conteúdo)
    }

}
