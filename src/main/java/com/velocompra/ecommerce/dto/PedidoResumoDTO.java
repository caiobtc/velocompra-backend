package com.velocompra.ecommerce.dto;

import com.velocompra.ecommerce.model.Pedido;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PedidoResumoDTO {
    private String numeroPedido;
    private LocalDateTime dataCriacao;
    private BigDecimal valorTotal;
    private String status;

    public PedidoResumoDTO(Pedido pedido) {
        this.numeroPedido = pedido.getNumeroPedido();
        this.dataCriacao = pedido.getDataCriacao();
        this.valorTotal = pedido.getValorTotal();
        this.status = pedido.getStatus().name();
    }

    public PedidoResumoDTO() {
    }
}
