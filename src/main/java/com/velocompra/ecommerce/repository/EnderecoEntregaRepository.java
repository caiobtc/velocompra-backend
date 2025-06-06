package com.velocompra.ecommerce.repository;

import com.velocompra.ecommerce.model.EnderecoEntrega;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositório responsável pela persistência de dados relacionados aos endereços de entrega.
 * Este repositório fornece métodos para recuperar um endereço de entrega com base no ID do endereço
 * e no ID do cliente associado.
 */
public interface EnderecoEntregaRepository extends JpaRepository<EnderecoEntrega, Long> {

    /**
     * Encontra um endereço de entrega específico com base no ID do endereço e no ID do cliente.
     *
     * @param enderecoId O ID do endereço de entrega.
     * @param clienteId O ID do cliente associado ao endereço.
     * @return Um {@link Optional} contendo o endereço de entrega encontrado, ou {@link Optional#empty()} caso o endereço não seja encontrado.
     */
    Optional<EnderecoEntrega> findByIdAndClienteId(Long enderecoId, Long clienteId);
}
