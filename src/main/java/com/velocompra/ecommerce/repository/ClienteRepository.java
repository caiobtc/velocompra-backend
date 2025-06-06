package com.velocompra.ecommerce.repository;

import com.velocompra.ecommerce.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repositório responsável pela persistência de dados relacionados aos clientes.
 * Este repositório fornece métodos para verificar a existência de clientes pelo CPF ou e-mail,
 * bem como recuperar um cliente com base no e-mail.
 */
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    /**
     * Verifica se já existe um cliente com o CPF fornecido.
     *
     * @param cpf O CPF a ser verificado.
     * @return {@code true} se um cliente com o CPF fornecido já existir, {@code false} caso contrário.
     */
    boolean existsByCpf(String cpf);

    /**
     * Verifica se já existe um cliente com o e-mail fornecido.
     *
     * @param email O e-mail a ser verificado.
     * @return {@code true} se um cliente com o e-mail fornecido já existir, {@code false} caso contrário.
     */
    boolean existsByEmail(String email);

    /**
     * Encontra um cliente com base no e-mail fornecido.
     *
     * @param email O e-mail do cliente a ser encontrado.
     * @return Um {@link Optional} contendo o cliente encontrado, ou {@link Optional#empty()} caso nenhum cliente seja encontrado.
     */
    Optional<Cliente> findByEmail(String email);
}
