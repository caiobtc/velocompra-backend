package com.velocompra.ecommerce.model;

import com.velocompra.ecommerce.model.Grupo;
import jakarta.persistence.*;
import lombok.Data;

/**
 * Representa um usuário do sistema.
 * A classe contém informações essenciais do usuário, como nome, e-mail, senha, CPF, grupo (ADMIN, ESTOQUISTA, CLIENTE),
 * e status de ativação.
 */
@Data
@Entity
@Table(name = "usuarios")
public class Usuario {

    /**
     * Identificador único do usuário.
     * Gerado automaticamente pelo banco de dados.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nome completo do usuário.
     * Este campo é obrigatório.
     */
    private String nome;

    /**
     * Endereço de e-mail do usuário.
     * Este campo é obrigatório e deve ser único no sistema.
     */
    @Column(unique = true, nullable = false)
    private String email;

    /**
     * Senha do usuário.
     * Este campo é obrigatório e utilizado para autenticação do usuário.
     */
    private String senha;

    /**
     * Grupo do usuário, determinando seu nível de acesso.
     * O grupo pode ser ADMIN, ESTOQUISTA ou CLIENTE.
     */
    @Enumerated(EnumType.STRING)
    private Grupo grupo; // ADMIN, ESTOQUISTA, CLIENTE

    /**
     * CPF do usuário.
     * Este campo é obrigatório e deve ser único no sistema.
     */
    private String cpf;

    /**
     * Indica se o usuário está ativo no sistema.
     * O valor padrão é {@code true}, indicando que o usuário está ativo e pode acessar o sistema.
     */
    private boolean ativo = true;
}
