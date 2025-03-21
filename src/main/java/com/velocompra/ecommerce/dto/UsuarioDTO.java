package com.velocompra.ecommerce.dto;

import com.velocompra.ecommerce.model.Grupo;
import lombok.Data;

@Data
public class UsuarioDTO {

    private String nome;
    private String cpf;
    private String email;      // Só para cadastro (não para edição)
    private String senha;
    private Grupo grupo;
}
