package com.velocompra.ecommerce.model;

/**
 * Enum que representa os diferentes status de um pedido no sistema.
 * O status do pedido indica em qual etapa do processo de compra o pedido se encontra.
 */
public enum StatusPedido {

    /**
     * O pedido está aguardando o pagamento.
     * Este status é atribuído quando o pedido é criado, mas o pagamento ainda não foi realizado.
     */
    AGUARDANDO_PAGAMENTO,

    /**
     * O pagamento do pedido foi rejeitado.
     * Este status é atribuído quando o pagamento não é aprovado ou é rejeitado pelo sistema de pagamento.
     */
    PAGAMENTO_REJEITADO,

    /**
     * O pagamento do pedido foi realizado com sucesso.
     * Este status é atribuído quando o pagamento do pedido é confirmado com sucesso.
     */
    PAGAMENTO_COM_SUCESSO,

    /**
     * O pedido está aguardando retirada.
     * Este status é atribuído quando o pedido está pronto para ser retirado pelo cliente.
     */
    AGUARDANDO_RETIRADA,

    /**
     * O pedido está em trânsito para o destino.
     * Este status é atribuído quando o pedido foi despachado para a entrega e está em processo de transporte.
     */
    EM_TRANSITO,

    /**
     * O pedido foi entregue ao cliente.
     * Este status é atribuído quando o pedido chega ao cliente e a entrega é concluída com sucesso.
     */
    ENTREGUE,

    PENDENTE,

    PROCESSANDO,

    CANCELADO,

    EM_PROCESSAMENTO,

    ENVIADO,

    CONCLUIDO
}
