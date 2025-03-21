package com.velocompra.ecommerce.service;

import com.velocompra.ecommerce.model.Grupo;
import com.velocompra.ecommerce.model.Usuario;
import com.velocompra.ecommerce.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Valida o login do usuário.
     *
     * @param email Email informado no login
     * @param senhaPura Senha pura informada no login
     * @return Usuario se válido, null se inválido
     */
    public Usuario validarLogin(String email, String senhaPura) {
        // Busca o usuário pelo email
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        // Se não encontrou usuário, retorna null (não autorizado)
        if (!usuarioOpt.isPresent()) {
            return null;
        }

        Usuario usuario = usuarioOpt.get();

        // Valida a senha (com o passwordEncoder que usa BCrypt)
        if (!passwordEncoder.matches(senhaPura, usuario.getSenha())) {
            return null;
        }

        // Se o usuário for CLIENTE, não é permitido no backoffice
        if (usuario.getGrupo() == Grupo.CLIENTE) {
            return null;
        }

        // Se o usuário não estiver habilitado (inativo), retorna null
        if (!usuario.isAtivo()) {
            return null;
        }

        // Login válido, retorna o usuário para ser usado no Controller
        return usuario;
    }
}
