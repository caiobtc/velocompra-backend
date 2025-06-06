package com.velocompra.ecommerce.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Representa um produto no sistema de e-commerce.
 * A classe contém informações detalhadas sobre o produto, incluindo nome, descrição, preço, quantidade em estoque,
 * status de ativação, imagens e a imagem padrão do produto.
 */
@Data
@Entity
@Table(name = "produtos")
public class Produto {

    public Produto() {}

    /**
     * Identificador único do produto.
     * Gerado automaticamente pelo banco de dados.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nome do produto.
     * Este campo é obrigatório e tem um comprimento máximo de 200 caracteres.
     */
    @Column(length = 200, nullable = false)
    private String nome;

    /**
     * Descrição detalhada do produto.
     * Este campo é obrigatório e tem um comprimento máximo de 2000 caracteres.
     */
    @Column(length = 2000, nullable = false)
    private String descricaoDetalhada;

    /**
     * Preço do produto.
     * Este campo é obrigatório e usa a precisão de 10 dígitos no total, com 2 casas decimais.
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    /**
     * Quantidade disponível em estoque do produto.
     * Este campo é obrigatório.
     */
    @Column(nullable = false)
    private int quantidadeEstoque;

    /**
     * Indica se o produto está ativo no sistema.
     * Se o produto estiver ativo, ele estará disponível para compra.
     * O valor padrão é {@code true}.
     */
    @Column(nullable = false)
    private boolean ativo = true;

    /**
     * Lista de imagens associadas ao produto.
     * Armazena os caminhos/nome dos arquivos das imagens.
     * As imagens são carregadas de forma imediata (EAGER fetch).
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "produto_imagens", joinColumns = @JoinColumn(name = "produto_id"))
    @Column(name = "imagem")
    private List<String> imagens;

    /**
     * Nome do arquivo ou URL relativa da imagem padrão do produto.
     * Define qual imagem será exibida como imagem principal do produto.
     */
    @Column(name = "imagem_padrao")
    private String imagemPadrao;

}
