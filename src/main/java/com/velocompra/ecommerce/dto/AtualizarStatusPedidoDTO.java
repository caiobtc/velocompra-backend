package com.velocompra.ecommerce.dto;

import com.velocompra.ecommerce.model.StatusPedido;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AtualizarStatusPedidoDTO {
    private StatusPedido novoStatusPedido;
}
