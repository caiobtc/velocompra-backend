package com.velocompra.ecommerce.service;

import com.velocompra.ecommerce.model.Cliente;
import com.velocompra.ecommerce.model.Usuario;
import com.velocompra.ecommerce.repository.ClienteRepository;
import com.velocompra.ecommerce.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Classe de testes unitários para a classe {@link AuthService}.
 * Utiliza JUnit 5 e Mockito para simular o comportamento dos repositórios e do codificador de senha.
 */

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AuthService authService;

    /**
     * Método executado antes de cada teste (@BeforeEach).
     * Serve para inicializar os mocks e a instância da classe que será testada.
     */
    @BeforeEach
    public void setUp() {
        authService = new AuthService();

        // Injetando manualmente os mocks, pois não estamos usando @InjectMocks
        authService.clienteRepository = clienteRepository;
        authService.usuarioRepository = usuarioRepository;
        authService.passwordEncoder = passwordEncoder;
    }

    /**
     * Teste de unidade (unitário).
     * Verifica se o login do usuário é validado corretamente quando todos os dados estão corretos.
     */
    @Test
    @DisplayName("Deve retornar um usuário válido quando o email, senha e status estão corretos")
    public void testValidarLoginUsuario_ComSucesso() {
        Usuario usuario = new Usuario();
        usuario.setEmail("teste@velocompra.com");
        usuario.setSenha("senhaCriptografada");
        usuario.setAtivo(true);

        when(usuarioRepository.findByEmail("teste@velocompra.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senha123", "senhaCriptografada")).thenReturn(true);

        Usuario resultado = authService.validarLogin("teste@velocompra.com", "senha123");

        assertNotNull(resultado);
        assertEquals("teste@velocompra.com", resultado.getEmail());
    }

    /**
     * Teste de unidade para o cenário onde o e-mail do usuário não é encontrado no banco.
     * Usa o método when() do Mockito para simular o retorno vazio (Optional.empty()).
     */
    @Test
    @DisplayName("Deve retornar null quando o usuário não é encontrado")
    public void testValidarLoginUsuario_EmailInexistente() {
        when(usuarioRepository.findByEmail("naoexiste@velocompra.com")).thenReturn(Optional.empty());

        Usuario resultado = authService.validarLogin("naoexiste@velocompra.com", "senha123");

        assertNull(resultado);
    }

    /**
     * Teste de unidade para o cenário em que a senha informada não corresponde à armazenada.
     */
    @Test
    @DisplayName("Deve retornar null quando a senha está incorreta")
    public void testValidarLoginUsuario_SenhaIncorreta() {
        Usuario usuario = new Usuario();
        usuario.setEmail("teste@velocompra.com");
        usuario.setSenha("senhaCriptografada");
        usuario.setAtivo(true);

        when(usuarioRepository.findByEmail("teste@velocompra.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senhaErrada", "senhaCriptografada")).thenReturn(false);

        Usuario resultado = authService.validarLogin("teste@velocompra.com", "senhaErrada");

        assertNull(resultado);
    }

    /**
     * Teste de unidade para o caso em que o usuário está com status inativo.
     */
    @Test
    @DisplayName("Deve retornar null quando o usuário está inativo")
    public void testValidarLoginUsuario_UsuarioInativo() {
        Usuario usuario = new Usuario();
        usuario.setEmail("teste@velocompra.com");
        usuario.setSenha("senhaCriptografada");
        usuario.setAtivo(false); // usuário inativo

        when(usuarioRepository.findByEmail("teste@velocompra.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senha123", "senhaCriptografada")).thenReturn(true);

        Usuario resultado = authService.validarLogin("teste@velocompra.com", "senha123");

        assertNull(resultado);
    }

    /**
     * Teste de unidade para autenticação de cliente com sucesso.
     * Verifica se o método retorna o cliente corretamente quando os dados são válidos.
     */
    @Test
    @DisplayName("Deve retornar cliente válido quando e-mail e senha estão corretos")
    public void testValidarLoginCliente_ComSucesso() {
        Cliente cliente = new Cliente();
        cliente.setEmail("cliente@velocompra.com");
        cliente.setSenha("senhaCriptografada");

        when(clienteRepository.findByEmail("cliente@velocompra.com")).thenReturn(Optional.of(cliente));
        when(passwordEncoder.matches("senha123", "senhaCriptografada")).thenReturn(true);

        Cliente resultado = authService.validarLoginCliente("cliente@velocompra.com", "senha123");

        assertNotNull(resultado);
        assertEquals("cliente@velocompra.com", resultado.getEmail());
        assertEquals("senhaCriptografada", resultado.getSenha());
    }

    /**
     * Teste de unidade para cliente com senha inválida.
     * O método deve retornar null se a senha estiver incorreta.
     */
    @Test
    @DisplayName("Deve retornar null quando senha do cliente está incorreta")
    public void testValidarLoginCliente_SenhaIncorreta() {
        Cliente cliente = new Cliente();
        cliente.setEmail("cliente@velocompra.com");
        cliente.setSenha("senhaCriptografada");

        when(clienteRepository.findByEmail("cliente@velocompra.com")).thenReturn(Optional.of(cliente));
        when(passwordEncoder.matches("errada", "senhaCriptografada")).thenReturn(false);

        Cliente resultado = authService.validarLoginCliente("cliente@velocompra.com", "errada");

        assertNull(resultado);
    }

    /**
     * Teste de unidade para quando o cliente não é encontrado.
     * Deve retornar null ao buscar e-mail inexistente.
     */
    @Test
    @DisplayName("Deve retornar null quando cliente não for encontrado")
    public void testValidarLoginCliente_EmailInexistente() {
        when(clienteRepository.findByEmail("naoexiste@velocompra.com")).thenReturn(Optional.empty());

        Cliente resultado = authService.validarLoginCliente("naoexiste@velocompra.com", "senha123");

        assertNull(resultado);
    }
}
