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

/**
 * Controlador responsável pela gestão do checkout de um cliente.
 * Este controlador oferece endpoints para recuperar os dados necessários para a página de checkout,
 * incluindo os endereços de entrega do cliente.
 */
@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {

    @Autowired
    ClienteService clienteService;

    /**
     * Recupera os endereços de entrega do cliente autenticado para exibição na página de checkout.
     * Verifica se o cliente está autenticado e se possui endereços de entrega cadastrados.
     *
     * @return Uma resposta com a lista de endereços de entrega ou uma mensagem de erro caso o cliente não esteja autenticado ou não tenha endereços cadastrados.
     */
    @GetMapping
    public ResponseEntity<?> getCheckoutPage() {
        String email = getAuthenticatedUserEmail();

        // Verifica se o usuário está autenticado
        if (email == null) {
            return ResponseEntity.status(401).body("Você precisa estar logado para acessar o checkout.");
        }

        // Recupera o cliente autenticado
        Cliente cliente = clienteService.getClienteByEmail(email);
        if (cliente == null) {
            return ResponseEntity.status(404).body("Cliente não encontrado.");
        }

        // Verifica se o cliente possui endereços de entrega cadastrados
        List<EnderecoEntrega> enderecosEntrega = cliente.getEnderecosEntrega();
        if (enderecosEntrega.isEmpty()) {
            return ResponseEntity.status(400).body("Você precisa ter um endereço de entrega cadastrado.");
        }

        // Log para fins de depuração
        System.out.println("Authentication: " + SecurityContextHolder.getContext().getAuthentication());

        // Retorna a lista de endereços de entrega
        return ResponseEntity.ok(enderecosEntrega);
    }

    /**
     * Obtém o e-mail do cliente autenticado a partir do contexto de segurança.
     *
     * @return O e-mail do cliente autenticado ou null se o cliente não estiver autenticado.
     */
    private String getAuthenticatedUserEmail() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return (authentication != null && authentication.isAuthenticated()) ? authentication.getName() : null;
    }

    /**
     * Valida se o endereço de entrega fornecido está associado ao cliente.
     *
     * @param cliente O cliente cujos endereços de entrega serão verificados.
     * @param enderecoEntregaId O ID do endereço de entrega a ser validado.
     * @return O endereço de entrega correspondente ao ID fornecido, ou null se o endereço não for encontrado.
     */
    private EnderecoEntrega validarEnderecoEntrega(Cliente cliente, Long enderecoEntregaId) {
        return cliente.getEnderecosEntrega().stream()
                .filter(endereco -> endereco.getId().equals(enderecoEntregaId))
                .findFirst()
                .orElse(null);
    }
}
