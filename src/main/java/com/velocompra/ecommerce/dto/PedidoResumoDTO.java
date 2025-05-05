package com.velocompra.ecommerce.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PedidoResumoDTO {
    private String numeroPedido;
    private LocalDateTime dataCriacao;
    private BigDecimal valorTotal;
    private String status;
}
