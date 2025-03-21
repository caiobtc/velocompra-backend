package com.velocompra.ecommerce.repository;

import com.velocompra.ecommerce.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    List<Usuario> findByNomeContainingIgnoreCase(String nome);

    List<Usuario> findAll();
    Optional<Usuario> findByEmail(String email);

    List<Usuario> findByNomeContaining(String nome);
}
