package com.velocompra.ecommerce.controller;

import com.velocompra.ecommerce.model.Cliente;
import com.velocompra.ecommerce.model.EnderecoEntrega;
import com.velocompra.ecommerce.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {

    @Autowired
    ClienteService clienteService;

    @GetMapping
    public ResponseEntity<?> getCheckoutPage() {
        String email = getAuthenticatedUserEmail();
        if (email == null) {
            return ResponseEntity.status(401).body("Você precisa estar logado para acessar o checkout.");
        }

        Cliente cliente = clienteService.getClienteByEmail(email);
        if (cliente == null) {
            return ResponseEntity.status(404).body("Cliente não encontrado.");
        }

        List<EnderecoEntrega> enderecosEntrega = cliente.getEnderecosEntrega();
        if (enderecosEntrega.isEmpty()) {
            return ResponseEntity.status(400).body("Você precisa ter um endereço de entrega cadastrado.");
        }

        System.out.println("Authentication: " + SecurityContextHolder.getContext().getAuthentication());
        return ResponseEntity.ok(enderecosEntrega);
    }

    private String getAuthenticatedUserEmail() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return (authentication != null && authentication.isAuthenticated()) ? authentication.getName() : null;
    }

    /**
     * Valida se o endereço de entrega fornecido está associado ao cliente.
     */
    private EnderecoEntrega validarEnderecoEntrega(Cliente cliente, Long enderecoEntregaId) {
        return cliente.getEnderecosEntrega().stream()
                .filter(endereco -> endereco.getId().equals(enderecoEntregaId))
                .findFirst()
                .orElse(null);
    }
}
