package com.velocompra.ecommerce.repository;

import com.velocompra.ecommerce.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório responsável pela persistência de dados relacionados aos usuários.
 * Este repositório fornece métodos para buscar usuários com base em seu nome ou e-mail,
 * além de permitir a recuperação de todos os usuários.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Pesquisa usuários cujo nome contenha a string fornecida, ignorando maiúsculas e minúsculas.
     *
     * @param nome O nome ou parte do nome do usuário a ser buscado.
     * @return Uma lista de usuários cujo nome contém a string fornecida.
     */
    List<Usuario> findByNomeContainingIgnoreCase(String nome);

    /**
     * Recupera todos os usuários cadastrados.
     *
     * @return Uma lista de todos os usuários registrados no sistema.
     */
    List<Usuario> findAll();

    /**
     * Pesquisa um usuário pelo e-mail fornecido.
     *
     * @param email O e-mail do usuário a ser buscado.
     * @return Um {@link Optional} contendo o usuário encontrado, ou {@link Optional#empty()} caso não seja encontrado.
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Pesquisa usuários cujo nome contenha a string fornecida (caso sensível).
     *
     * @param nome O nome ou parte do nome do usuário a ser buscado.
     * @return Uma lista de usuários cujo nome contém a string fornecida.
     */
    List<Usuario> findByNomeContaining(String nome);
}
