package com.velocompra.ecommerce.repository;

import com.velocompra.ecommerce.model.EnderecoFaturamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnderecoFaturamentoRepository extends JpaRepository<EnderecoFaturamento, Long> {
}
