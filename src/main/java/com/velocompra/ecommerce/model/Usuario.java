package com.velocompra.ecommerce.model;

import com.velocompra.ecommerce.model.Grupo;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Column(unique = true, nullable = false)
    private String email;

    private String senha;

    @Enumerated(EnumType.STRING)
    private Grupo grupo; // ADMIN, ESTOQUISTA, CLIENTE

    private String cpf;

    private boolean ativo = true;

}
