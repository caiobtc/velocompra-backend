package com.velocompra.ecommerce.model;

/**
 * Enum que representa os diferentes grupos de usuários no sistema.
 * Os grupos determinam o nível de acesso e as permissões que o usuário possui.
 */
public enum Grupo {

    /**
     * Grupo de usuários com permissões administrativas.
     * Usuários no grupo ADMINISTRADOR têm acesso total ao sistema.
     */
    ADMINISTRADOR,

    /**
     * Grupo de usuários com permissões de estoquista.
     * Usuários no grupo ESTOQUISTA têm acesso para gerenciar o estoque de produtos.
     */
    ESTOQUISTA,

    /**
     * Grupo de usuários com permissões de cliente.
     * Usuários no grupo CLIENTE têm acesso para fazer compras e gerenciar seus próprios dados.
     */
    CLIENTE
}
