package com.velocompra.ecommerce.controller;

import com.velocompra.ecommerce.dto.LoginRequest;
import com.velocompra.ecommerce.dto.LoginResponse;
import com.velocompra.ecommerce.model.Cliente;
import com.velocompra.ecommerce.model.Grupo;
import com.velocompra.ecommerce.model.Usuario;
import com.velocompra.ecommerce.security.JWTUtil;
import com.velocompra.ecommerce.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador responsável pela autenticação de usuários e clientes.
 * Este controlador oferece endpoints para o login de usuários administradores e clientes.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class LoginController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JWTUtil jwtUtil;

    /**
     * Realiza o login de um usuário administrador.
     * Valida o e-mail e a senha do usuário e, se válido, gera um token JWT para autenticação.
     *
     * @param loginRequest O DTO contendo o e-mail e a senha para autenticação.
     * @return Uma resposta com o token JWT gerado ou uma mensagem de erro em caso de falha de autenticação.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Usuario usuario = authService.validarLogin(loginRequest.getEmail(), loginRequest.getSenha());

        // Se o usuário não for encontrado ou a senha estiver incorreta
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email ou senha inválidos.");
        }

        // Se o usuário for um cliente, não permite login
        if (usuario.getGrupo() != Grupo.ADMINISTRADOR) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso não permitido para clientes.");
        }

        // Gera o token JWT para o usuário
        String token = jwtUtil.generateToken(usuario);

        // Retorna o token junto com o nome e o grupo do usuário
        LoginResponse response = new LoginResponse(token, usuario.getNome(), usuario.getGrupo().name());
        return ResponseEntity.ok(response);
    }

    /**
     * Realiza o login de um cliente.
     * Valida o e-mail e a senha do cliente e, se válido, gera um token JWT para autenticação.
     *
     * @param loginRequest O DTO contendo o e-mail e a senha para autenticação.
     * @return Uma resposta com o token JWT gerado ou uma mensagem de erro em caso de falha de autenticação.
     */
    @PostMapping("/login-cliente")
    public ResponseEntity<?> loginCliente(@RequestBody LoginRequest loginRequest) {
        Cliente cliente = authService.validarLoginCliente(loginRequest.getEmail(), loginRequest.getSenha());

        // Se o cliente não for encontrado ou a senha estiver incorreta
        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email ou senha inválidos.");
        }

        // Gera o token JWT para o cliente
        String token = jwtUtil.generateTokenCliente(cliente);

        // Retorna o token junto com o nome do cliente e o grupo "CLIENTE"
        LoginResponse response = new LoginResponse(token, cliente.getNomeCompleto(), "CLIENTE");
        return ResponseEntity.ok(response);
    }
}
