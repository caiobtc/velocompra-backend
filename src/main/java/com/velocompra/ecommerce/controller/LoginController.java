package com.velocompra.ecommerce.controller;

import com.velocompra.ecommerce.dto.LoginRequest;
import com.velocompra.ecommerce.dto.LoginResponse;
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
    private JWTUtil jwtUtil; // Injetando a instância do JWTUtil

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        String senha = loginRequest.getSenha();

        Usuario usuario = authService.validarLogin(email, senha);

        if (usuario != null && usuario.isAtivo()) {
            String token = jwtUtil.generateToken(usuario);

            LoginResponse response = new LoginResponse(
                    token,
                    usuario.getNome(),
                    usuario.getGrupo().name()
            );

            return ResponseEntity.ok(response);

        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login falhou. Verifique suas credenciais.");
        }
    }
}
