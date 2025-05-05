package com.velocompra.ecommerce.repository;

import com.velocompra.ecommerce.model.EnderecoEntrega;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EnderecoEntregaRepository extends JpaRepository<EnderecoEntrega, Long> {

    Optional<EnderecoEntrega> findByIdAndClienteId(Long enderecoId, Long clienteId);
}
