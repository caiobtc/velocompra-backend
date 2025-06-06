package com.velocompra.ecommerce.service;

import com.velocompra.ecommerce.model.Cliente;
import com.velocompra.ecommerce.model.Usuario;
import com.velocompra.ecommerce.repository.ClienteRepository;
import com.velocompra.ecommerce.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Serviço responsável pela autenticação de usuários e clientes.
 * Contém métodos para validar o login de usuários e clientes, verificando o e-mail, senha e status de ativação.
 */
@Service
public class AuthService {

    @Autowired
    public UsuarioRepository usuarioRepository;

    @Autowired
    public ClienteRepository clienteRepository;

    @Autowired
    public PasswordEncoder passwordEncoder;

    /**
     * Valida o login de um usuário.
     * Verifica se o usuário existe, se a senha fornecida é válida e se o usuário está ativo.
     *
     * @param email O e-mail do usuário a ser autenticado.
     * @param senhaPura A senha fornecida pelo usuário.
     * @return O objeto {@link Usuario} se o login for válido, ou null caso contrário.
     */
    public Usuario validarLogin(String email, String senhaPura) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (!usuarioOpt.isPresent()) return null; // Se o usuário não existir, retorna null.

        Usuario usuario = usuarioOpt.get();
        if (!passwordEncoder.matches(senhaPura, usuario.getSenha())) return null; // Verifica se a senha fornecida é válida.
        if (!usuario.isAtivo()) return null; // Verifica se o usuário está ativo.

        return usuario; // Retorna o usuário autenticado.
    }

    /**
     * Valida o login de um cliente.
     * Verifica se o cliente existe e se a senha fornecida é válida.
     *
     * @param email O e-mail do cliente a ser autenticado.
     * @param senhaPura A senha fornecida pelo cliente.
     * @return O objeto {@link Cliente} se o login for válido, ou null caso contrário.
     */
    public Cliente validarLoginCliente(String email, String senhaPura) {
        Optional<Cliente> clienteOpt = clienteRepository.findByEmail(email);
        if (!clienteOpt.isPresent()) return null; // Se o cliente não existir, retorna null.

        Cliente cliente = clienteOpt.get();
        if (!passwordEncoder.matches(senhaPura, cliente.getSenha())) return null; // Verifica se a senha fornecida é válida.

        return cliente; // Retorna o cliente autenticado.
    }
}
