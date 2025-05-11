package com.velocompra.ecommerce.repository;

import com.velocompra.ecommerce.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    Pedido findTopByOrderByNumeroPedidoDesc(); // Busca o último número de pedido gerado
    List<Pedido> findByClienteId(Long clienteId);
    List<Pedido> findAllByOrderByDataCriacaoDesc();
    Optional<Pedido> findByNumeroPedido(String numeroPedido);
}
