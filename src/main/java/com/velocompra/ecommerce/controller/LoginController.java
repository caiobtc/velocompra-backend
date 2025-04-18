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

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class LoginController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JWTUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Usuario usuario = authService.validarLogin(loginRequest.getEmail(), loginRequest.getSenha());

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email ou senha inválidos.");
        }

        if (usuario.getGrupo() == Grupo.CLIENTE) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso não permitido para clientes.");
        }

        String token = jwtUtil.generateToken(usuario);
        LoginResponse response = new LoginResponse(token, usuario.getNome(), usuario.getGrupo().name());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login-cliente")
    public ResponseEntity<?> loginCliente(@RequestBody LoginRequest loginRequest) {
        Cliente cliente = authService.validarLoginCliente(loginRequest.getEmail(), loginRequest.getSenha());

        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email ou senha inválidos.");
        }

        String token = jwtUtil.generateTokenCliente(cliente);
        LoginResponse response = new LoginResponse(token, cliente.getNomeCompleto(), "CLIENTE");
        return ResponseEntity.ok(response);
    }
}