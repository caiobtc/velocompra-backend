package com.velocompra.ecommerce.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Representa um item de um pedido.
 * A classe contém as informações sobre o produto, quantidade e preço unitário de um item dentro de um pedido.
 * Cada item está associado a um pedido e a um produto específico.
 */
@Data
@Entity
public class ItemPedido {

    /**
     * Identificador único do item do pedido.
     * Gerado automaticamente pelo banco de dados.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Relacionamento de muitos para um (Many-to-One) com a entidade {@link Pedido}.
     * Cada item pertence a um pedido específico.
     */
    @ManyToOne
    private Pedido pedido;

    /**
     * Relacionamento de muitos para um (Many-to-One) com a entidade {@link Produto}.
     * Cada item é relacionado a um produto específico.
     */
    @ManyToOne
    private Produto produto;

    /**
     * Quantidade do produto no item do pedido.
     * Indica quantas unidades do produto foram compradas no pedido.
     */
    private int quantidade;

    /**
     * Preço unitário do produto no momento da compra.
     * Este valor é utilizado para calcular o valor total do item (quantidade * preço unitário).
     */
    private BigDecimal precoUnitario;
}
