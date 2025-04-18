package com.velocompra.ecommerce.service;

import com.velocompra.ecommerce.model.Cliente;
import com.velocompra.ecommerce.model.Usuario;
import com.velocompra.ecommerce.repository.ClienteRepository;
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
    private ClienteRepository clienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario validarLogin(String email, String senhaPura) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (!usuarioOpt.isPresent()) return null;

        Usuario usuario = usuarioOpt.get();
        if (!passwordEncoder.matches(senhaPura, usuario.getSenha())) return null;
        if (!usuario.isAtivo()) return null;

        return usuario;
    }

    public Cliente validarLoginCliente(String email, String senhaPura) {
        Optional<Cliente> clienteOpt = clienteRepository.findByEmail(email);
        if (!clienteOpt.isPresent()) return null;

        Cliente cliente = clienteOpt.get();
        if (!passwordEncoder.matches(senhaPura, cliente.getSenha())) return null;

        return cliente;
    }
}