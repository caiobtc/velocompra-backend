package com.velocompra.ecommerce.repository;

import com.velocompra.ecommerce.model.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    // Pesquisa com filtro de nome (opcional)
    List<Produto> findByNomeContainingIgnoreCase(String nome);
    Page<Produto> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
    Page<Produto> findByAtivoTrue(Pageable pageable);
}
