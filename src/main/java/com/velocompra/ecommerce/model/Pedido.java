package com.velocompra.ecommerce.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa um pedido realizado por um cliente no sistema.
 * A classe contém informações sobre o número do pedido, valor total, status, cliente, endereço de entrega,
 * forma de pagamento, valor do frete, data de criação e os itens do pedido.
 */
@Data
@Entity
public class Pedido {

    /**
     * Identificador único do pedido.
     * Gerado automaticamente pelo banco de dados.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Número do pedido, um identificador único gerado para cada pedido.
     */
    private String numeroPedido;

    /**
     * Valor total do pedido, incluindo o valor dos produtos e o valor do frete.
     */
    private BigDecimal valorTotal;

    /**
     * Status do pedido, que indica em qual etapa o pedido se encontra (ex: "Aguardando Pagamento", "Em Processamento", etc.).
     * O status é representado por um enum {@link StatusPedido}.
     */
    @Enumerated(EnumType.STRING)
    private StatusPedido status;

    /**
     * Relacionamento de muitos para um (Many-to-One) com a entidade {@link Cliente}.
     * Cada pedido está associado a um cliente específico.
     */
    @ManyToOne
    private Cliente cliente;

    /**
     * Relacionamento de muitos para um (Many-to-One) com a entidade {@link EnderecoEntrega}.
     * Cada pedido possui um endereço de entrega.
     */
    @ManyToOne
    private EnderecoEntrega enderecoEntrega;

    /**
     * Forma de pagamento utilizada no pedido (ex: Cartão de Crédito, Boleto, etc.).
     */
    private String formaPagamento;

    /**
     * Valor do frete associado ao pedido.
     */
    private BigDecimal frete;

    /**
     * Data e hora de criação do pedido.
     * O valor é automaticamente atribuído ao momento da criação do pedido.
     */
    private LocalDateTime dataCriacao = LocalDateTime.now();

    /**
     * Relacionamento de um para muitos (One-to-Many) com a entidade {@link ItemPedido}.
     * Cada pedido contém uma lista de itens, representando os produtos adquiridos.
     * A exclusão de um pedido também exclui seus itens relacionados.
     */
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private List<ItemPedido> itens = new ArrayList<>();
}
