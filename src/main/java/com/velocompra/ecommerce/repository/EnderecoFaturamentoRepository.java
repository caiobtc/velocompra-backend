package com.velocompra.ecommerce.repository;

import com.velocompra.ecommerce.model.EnderecoFaturamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositório responsável pela persistência de dados relacionados aos endereços de faturamento.
 * Este repositório fornece operações básicas de persistência, como salvar, atualizar, excluir e buscar endereços de faturamento.
 */
@Repository
public interface EnderecoFaturamentoRepository extends JpaRepository<EnderecoFaturamento, Long> {
}
