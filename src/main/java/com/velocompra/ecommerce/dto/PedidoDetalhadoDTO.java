package com.velocompra.ecommerce.dto;

import com.velocompra.ecommerce.model.StatusPedido;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PedidoDetalhadoDTO {
    private String numeroPedido;
    private LocalDateTime dataCriacao;
    private BigDecimal valorFrete;
    private BigDecimal valorTotal;
    private String formaPagamento;
    private StatusPedido status;

    private EnderecoDTO enderecoEntrega;

    private List<ItemPedidoDTO> itens;

    @Getter
    @Setter
    public static class ItemPedidoDTO {
        private String nomeProduto;
        private Integer quantidade;
        private BigDecimal precoUnitario;
        private BigDecimal precoTotal;
        private String imagemProduto;
    }
}
