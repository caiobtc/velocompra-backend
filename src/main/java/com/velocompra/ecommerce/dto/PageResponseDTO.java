package com.velocompra.ecommerce.dto;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Data Transfer Object (DTO) utilizado para representar uma resposta paginada.
 * Esta classe é usada para transferir dados de uma página de resultados, como conteúdo, número da página,
 * tamanho da página, total de elementos, total de páginas e se a página é a última.
 *
 * @param <T> Tipo genérico que representa os dados contidos na página.
 */
@Data
public class PageResponseDTO<T> {

    /**
     * Conteúdo da página.
     * Uma lista de objetos do tipo {@link T} contendo os dados da página.
     */
    private List<T> content;

    /**
     * Número da página atual.
     * Representa a página que está sendo retornada na consulta paginada.
     */
    private int pageNumber;

    /**
     * Tamanho da página.
     * Representa o número de elementos por página.
     */
    private int pageSize;

    /**
     * Total de elementos disponíveis.
     * Representa o número total de registros disponíveis, independentemente da paginação.
     */
    private long totalElements;

    /**
     * Total de páginas.
     * Representa o número total de páginas disponíveis com base no número de elementos por página.
     */
    private int totalPages;

    /**
     * Indicador se a página atual é a última.
     * Se for {@code true}, significa que a página retornada é a última página.
     */
    private boolean last;

    /**
     * Construtor que converte uma instância de {@link Page} para o DTO {@link PageResponseDTO}.
     * Preenche os campos do DTO com os dados da página fornecida.
     *
     * @param page A página de resultados do tipo {@link Page}.
     *             A página é um objeto do Spring Data que contém os dados da página solicitada.
     */
    public PageResponseDTO(Page<T> page) {
        this.content = page.getContent();
        this.pageNumber = page.getNumber();
        this.pageSize = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.last = page.isLast();
    }
}
