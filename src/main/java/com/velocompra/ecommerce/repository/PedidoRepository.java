package com.velocompra.ecommerce.repository;

import com.velocompra.ecommerce.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório responsável pela persistência de dados relacionados aos pedidos.
 * Este repositório oferece métodos para buscar pedidos com base em critérios específicos,
 * como o ID do cliente, o número do pedido, e a data de criação.
 */
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    /**
     * Busca o último número de pedido gerado no sistema.
     *
     * @return O último pedido registrado no banco de dados, baseado no número do pedido em ordem decrescente.
     */
    Pedido findTopByOrderByNumeroPedidoDesc();

    /**
     * Busca todos os pedidos de um cliente com base no ID do cliente.
     *
     * @param clienteId O ID do cliente cujos pedidos serão buscados.
     * @return Uma lista de pedidos associados ao cliente especificado.
     */
    List<Pedido> findByClienteId(Long clienteId);

    /**
     * Lista todos os pedidos, ordenados pela data de criação de forma decrescente.
     *
     * @return Uma lista de todos os pedidos, ordenados por data de criação, do mais recente para o mais antigo.
     */
    List<Pedido> findAllByOrderByDataCriacaoDesc();

    /**
     * Busca um pedido específico com base no número do pedido.
     *
     * @param numeroPedido O número do pedido a ser buscado.
     * @return Um {@link Optional} contendo o pedido encontrado, ou {@link Optional#empty()} caso não seja encontrado.
     */
    Optional<Pedido> findByNumeroPedido(String numeroPedido);
}
