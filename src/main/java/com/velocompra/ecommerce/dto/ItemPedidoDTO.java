package com.velocompra.ecommerce.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemPedidoDTO {

    private Long produtoId;
    private int quantidade;
    private BigDecimal precoUnitario;
}
