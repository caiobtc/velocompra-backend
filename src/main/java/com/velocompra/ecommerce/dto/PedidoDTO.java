package com.velocompra.ecommerce.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PedidoDTO {

    private Long enderecoEntregaId;
    private String formaPagamento;
    private BigDecimal frete;
    private List<ItemPedidoDTO> produtos;
}
