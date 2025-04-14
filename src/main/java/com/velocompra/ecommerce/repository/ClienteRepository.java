package com.velocompra.ecommerce.repository;

import com.velocompra.ecommerce.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email);

    // Você também pode adicionar outros métodos de busca se precisar, por exemplo:
    // Optional<Cliente> findByEmail(String email);
}
