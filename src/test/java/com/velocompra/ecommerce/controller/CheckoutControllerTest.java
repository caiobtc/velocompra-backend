package com.velocompra.ecommerce.controller;

import com.velocompra.ecommerce.model.Cliente;
import com.velocompra.ecommerce.model.EnderecoEntrega;
import com.velocompra.ecommerce.service.ClienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Classe de testes para o {@link CheckoutController}.
 * Contém testes unitários para os endpoints relacionados ao checkout,
 * focando na recuperação dos endereços de entrega do cliente autenticado.
 */
@ExtendWith(MockitoExtension.class)
class CheckoutControllerTest {

    @InjectMocks
    private CheckoutController checkoutController;

    @Mock
    private ClienteService clienteService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private final String EMAIL_TESTE = "cliente@teste.com";

    @BeforeEach
    void setup() {
        // Configura o SecurityContextHolder para retornar o contexto de segurança mockado
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    /**
     * Testa se o método {@code getCheckoutPage} retorna uma lista de endereços de entrega
     * quando um cliente está autenticado e possui endereços cadastrados.
     */
    @Test
    @DisplayName("Deve retornar endereços de entrega quando o cliente está autenticado e tem endereços")
    void deveRetornarEnderecosDeEntregaQuandoClienteAutenticadoETemEnderecos() {
        // Cenário
        Cliente cliente = new Cliente();
        EnderecoEntrega endereco1 = new EnderecoEntrega();
        endereco1.setId(1L);
        endereco1.setCep("12345678");
        EnderecoEntrega endereco2 = new EnderecoEntrega();
        endereco2.setId(2L);
        endereco2.setCep("87654321");
        cliente.setEnderecosEntrega(List.of(endereco1, endereco2));

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(EMAIL_TESTE);
        when(clienteService.getClienteByEmail(EMAIL_TESTE)).thenReturn(cliente);

        // Ação
        ResponseEntity<?> response = checkoutController.getCheckoutPage();

        // Verificação
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat((List<EnderecoEntrega>) response.getBody(), is(cliente.getEnderecosEntrega()));

        verify(clienteService, times(1)).getClienteByEmail(EMAIL_TESTE);
    }

    /**
     * Testa se o método {@code getCheckoutPage} retorna um erro 401 (Unauthorized)
     * quando não há um cliente autenticado.
     */
    @Test
    @DisplayName("Deve retornar erro 401 quando o cliente não está autenticado")
    void deveRetornarErro401QuandoClienteNaoEstaAutenticado() {
        // Cenário
        when(authentication.isAuthenticated()).thenReturn(false); // Simula que não há autenticação

        // Ação
        ResponseEntity<?> response = checkoutController.getCheckoutPage();

        // Verificação
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Você precisa estar autenticado para acessar essa página.", response.getBody());

        verify(clienteService, never()).getClienteByEmail(anyString());
    }

    /**
     * Testa se o método {@code getCheckoutPage} retorna um erro 400 (Bad Request)
     * quando o cliente autenticado não possui nenhum endereço de entrega cadastrado.
     */
    @Test
    @DisplayName("Deve retornar erro 400 quando o cliente autenticado não tem endereços de entrega")
    void deveRetornarErro400QuandoClienteAutenticadoNaoTemEnderecosDeEntrega() {
        // Cenário
        Cliente cliente = new Cliente();
        cliente.setEnderecosEntrega(Collections.emptyList()); // Cliente sem endereços

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(EMAIL_TESTE);
        when(clienteService.getClienteByEmail(EMAIL_TESTE)).thenReturn(cliente);

        // Ação
        ResponseEntity<?> response = checkoutController.getCheckoutPage();

        // Verificação
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Você precisa ter um endereço de entrega cadastrado.", response.getBody());

        verify(clienteService, times(1)).getClienteByEmail(EMAIL_TESTE);
    }

    /**
     * Testa se o método {@code getCheckoutPage} retorna um erro 404 (Not Found)
     * quando o cliente não é encontrado pelo e-mail, mesmo estando autenticado.
     */
    @Test
    @DisplayName("Deve retornar erro 404 quando o cliente autenticado não é encontrado")
    void deveRetornarErro404QuandoClienteAutenticadoNaoEhEncontrado() {
        // Cenário
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(EMAIL_TESTE);
        when(clienteService.getClienteByEmail(EMAIL_TESTE)).thenReturn(null); // Cliente não encontrado

        // Ação
        ResponseEntity<?> response = checkoutController.getCheckoutPage();

        // Verificação
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Cliente não encontrado.", response.getBody());

        verify(clienteService, times(1)).getClienteByEmail(EMAIL_TESTE);
    }
}