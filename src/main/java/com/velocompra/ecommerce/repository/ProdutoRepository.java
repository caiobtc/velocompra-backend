package com.velocompra.ecommerce.repository;

import com.velocompra.ecommerce.model.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositório responsável pela persistência de dados relacionados aos produtos.
 * Este repositório oferece métodos para buscar produtos com base no nome e no status de ativo,
 * com suporte para paginação e filtragem.
 */
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    /**
     * Pesquisa produtos com base no nome, ignorando maiúsculas e minúsculas.
     * Retorna uma lista de produtos que contém a string fornecida no nome.
     *
     * @param nome O nome ou parte do nome do produto a ser buscado.
     * @return Uma lista de produtos cujo nome contém a string fornecida.
     */
    List<Produto> findByNomeContainingIgnoreCase(String nome);

    /**
     * Pesquisa produtos com base no nome, com paginação, ignorando maiúsculas e minúsculas.
     * Retorna uma página de produtos que contém a string fornecida no nome.
     *
     * @param nome O nome ou parte do nome do produto a ser buscado.
     * @param pageable Parâmetros de paginação.
     * @return Uma página de produtos cujo nome contém a string fornecida.
     */
    Page<Produto> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    /**
     * Lista todos os produtos que estão ativos, com suporte para paginação.
     *
     * @param pageable Parâmetros de paginação.
     * @return Uma página de produtos que estão ativos (status 'ativo' igual a true).
     */
    Page<Produto> findByAtivoTrue(Pageable pageable);
}
