package com.velocompra.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.velocompra.ecommerce.dto.LoginRequest;
import com.velocompra.ecommerce.dto.LoginResponse;
import com.velocompra.ecommerce.model.Cliente;
import com.velocompra.ecommerce.model.Grupo;
import com.velocompra.ecommerce.model.Usuario;
import com.velocompra.ecommerce.security.JWTUtil;
import com.velocompra.ecommerce.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.config.annotation.EnableWebMvc; // Importe esta anotação

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Classe de teste para {@link LoginController}.
 * Utiliza JUnit 5, Mockito e MockMvc para testar os endpoints de autenticação.
 * Foca em simular requisições HTTP e verificar as respostas do controlador.
 */
@ExtendWith(MockitoExtension.class) // Habilita a integração do Mockito com JUnit 5
@EnableWebMvc // ADICIONADO: Garante que o Spring MVC seja configurado corretamente para o MockMvc.
public class LoginControllerTest {

    @InjectMocks // Injeta mocks nas dependências do controlador
    private LoginController loginController;

    @Mock // Cria um mock para AuthService, que é uma dependência do LoginController
    private AuthService authService;

    @Mock // Cria um mock para JWTUtil, que é uma dependência do LoginController
    private JWTUtil jwtUtil;

    private MockMvc mockMvc; // Objeto para simular requisições HTTP

    private ObjectMapper objectMapper; // Para converter objetos Java em JSON e vice-versa

    /**
     * Configurações iniciais para cada teste.
     * Inicializa o MockMvc para o controlador e o ObjectMapper.
     */
    @BeforeEach
    void setUp() {
        // Constrói o MockMvc para testar o LoginController isoladamente
        // O @EnableWebMvc ajuda a garantir que os mappings sejam encontrados.
        mockMvc = MockMvcBuilders.standaloneSetup(loginController).build();
        objectMapper = new ObjectMapper(); // Inicializa o ObjectMapper
    }

    /**
     * Testa o cenário de sucesso para o login de um usuário administrador.
     * Espera que o controlador retorne um status 200 OK e um {@link LoginResponse} com token e dados do usuário.
     *
     * @throws Exception se ocorrer um erro durante a simulação da requisição.
     */
    @Test
    @DisplayName("Deve realizar login de administrador com sucesso e retornar token")
    void deveRealizarLoginAdminComSucessoERetornarToken() throws Exception {
        // Cenário: Um usuário administrador com credenciais válidas.
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("admin@example.com");
        loginRequest.setSenha("senhaAdmin");

        Usuario usuario = new Usuario();
        usuario.setEmail("admin@example.com");
        usuario.setNome("Admin Teste");
        usuario.setGrupo(Grupo.ADMINISTRADOR); // Usuário do grupo ADMINISTRADOR

        String tokenGerado = "fakeJwtTokenAdmin";

        // Comportamento esperado dos mocks:
        // Quando authService.validarLogin é chamado, retorna o usuário.
        when(authService.validarLogin(loginRequest.getEmail(), loginRequest.getSenha())).thenReturn(usuario);
        // Quando jwtUtil.generateToken é chamado, retorna um token.
        when(jwtUtil.generateToken(usuario)).thenReturn(tokenGerado);

        // Ação: Realiza uma requisição POST para o endpoint de login de usuário
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON) // Define o tipo de conteúdo como JSON
                        .content(objectMapper.writeValueAsString(loginRequest))) // Converte o DTO para JSON
                // Verificação:
                .andExpect(status().isOk()) // Espera um status HTTP 200 OK
                .andExpect(jsonPath("$.token", is(equalTo(tokenGerado)))) // Verifica se o token está correto
                .andExpect(jsonPath("$.nome", is(equalTo(usuario.getNome())))) // Verifica se o nome está correto
                .andExpect(jsonPath("$.grupo", is(equalTo(usuario.getGrupo().name())))); // Verifica se o grupo está correto

        // Verifica se os métodos dos mocks foram chamados uma vez
        verify(authService, times(1)).validarLogin(loginRequest.getEmail(), loginRequest.getSenha());
        verify(jwtUtil, times(1)).generateToken(usuario);
    }

    /**
     * Testa o cenário de falha para o login de um usuário quando as credenciais são inválidas.
     * Espera que o controlador retorne um status 401 Unauthorized.
     *
     * @throws Exception se ocorrer um erro durante a simulação da requisição.
     */
    @Test
    @DisplayName("Deve retornar 401 Unauthorized quando credenciais de administrador são inválidas")
    void deveRetornar401QuandoCredenciaisAdminInvalidas() throws Exception {
        // Cenário: Credenciais de usuário inválidas.
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("admin@example.com");
        loginRequest.setSenha("senhaIncorreta");

        // Comportamento esperado do mock: authService.validarLogin retorna null (login falhou).
        when(authService.validarLogin(loginRequest.getEmail(), loginRequest.getSenha())).thenReturn(null);

        // Ação: Realiza uma requisição POST para o endpoint de login de usuário
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                // Verificação: Espera um status HTTP 401 Unauthorized.
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$", is(equalTo("Email ou senha inválidos.")))); // Verifica a mensagem de erro.

        // Verifica se authService.validarLogin foi chamado uma vez e jwtUtil nunca foi chamado.
        verify(authService, times(1)).validarLogin(loginRequest.getEmail(), loginRequest.getSenha());
        verifyNoInteractions(jwtUtil); // Não deve gerar token se o login falhar
    }

    /**
     * Testa o cenário de falha para o login de um usuário quando o usuário não pertence ao grupo ADMINISTRADOR.
     * Espera que o controlador retorne um status 403 Forbidden.
     *
     * @throws Exception se ocorrer um erro durante a simulação da requisição.
     */
    @Test
    @DisplayName("Deve retornar 403 Forbidden quando usuário não é administrador")
    void deveRetornar403QuandoUsuarioNaoEhAdmin() throws Exception {
        // Cenário: Um usuário válido, mas que não é ADMINISTRADOR (por exemplo, ESTOQUISTA).
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("estoquista@example.com");
        loginRequest.setSenha("senhaEstoquista");

        Usuario usuario = new Usuario();
        usuario.setEmail("estoquista@example.com");
        usuario.setNome("Estoquista Teste");
        usuario.setGrupo(Grupo.ESTOQUISTA); // Usuário do grupo ESTOQUISTA

        // Comportamento esperado do mock: authService.validarLogin retorna o usuário.
        when(authService.validarLogin(loginRequest.getEmail(), loginRequest.getSenha())).thenReturn(usuario);

        // Ação: Realiza uma requisição POST para o endpoint de login de usuário
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                // Verificação: Espera um status HTTP 403 Forbidden.
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$", is(equalTo("Acesso não permitido para clientes.")))); // Verifica a mensagem de erro.

        // Verifica se authService.validarLogin foi chamado uma vez e jwtUtil nunca foi chamado.
        verify(authService, times(1)).validarLogin(loginRequest.getEmail(), loginRequest.getSenha());
        verifyNoInteractions(jwtUtil); // Não deve gerar token se o grupo for inválido
    }

    /**
     * Testa o cenário de sucesso para o login de um cliente.
     * Espera que o controlador retorne um status 200 OK e um {@link LoginResponse} com token e dados do cliente.
     *
     * @throws Exception se ocorrer um erro durante a simulação da requisição.
     */
    @Test
    @DisplayName("Deve realizar login de cliente com sucesso e retornar token")
    void deveRealizarLoginClienteComSucessoERetornarToken() throws Exception {
        // Cenário: Um cliente com credenciais válidas.
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("cliente@example.com");
        loginRequest.setSenha("senhaCliente");

        Cliente cliente = new Cliente();
        cliente.setEmail("cliente@example.com");
        cliente.setNomeCompleto("Cliente Teste");

        String tokenGerado = "fakeJwtTokenCliente";

        // Comportamento esperado dos mocks:
        // Quando authService.validarLoginCliente é chamado, retorna o cliente.
        when(authService.validarLoginCliente(loginRequest.getEmail(), loginRequest.getSenha())).thenReturn(cliente);
        // Quando jwtUtil.generateTokenCliente é chamado, retorna um token.
        when(jwtUtil.generateTokenCliente(cliente)).thenReturn(tokenGerado);

        // Ação: Realiza uma requisição POST para o endpoint de login de cliente
        mockMvc.perform(post("/api/auth/login-cliente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                // Verificação:
                .andExpect(status().isOk()) // Espera um status HTTP 200 OK
                .andExpect(jsonPath("$.token", is(equalTo(tokenGerado)))) // Verifica se o token está correto
                .andExpect(jsonPath("$.nome", is(equalTo(cliente.getNomeCompleto())))) // Verifica se o nome está correto
                .andExpect(jsonPath("$.grupo", is(equalTo("CLIENTE")))); // Verifica se o grupo é "CLIENTE"

        // Verifica se os métodos dos mocks foram chamados uma vez
        verify(authService, times(1)).validarLoginCliente(loginRequest.getEmail(), loginRequest.getSenha());
        verify(jwtUtil, times(1)).generateTokenCliente(cliente);
    }

    /**
     * Testa o cenário de falha para o login de um cliente quando as credenciais são inválidas.
     * Espera que o controlador retorne um status 401 Unauthorized.
     *
     * @throws Exception se ocorrer um erro durante a simulação da requisição.
     */
    @Test
    @DisplayName("Deve retornar 401 Unauthorized quando credenciais de cliente são inválidas")
    void deveRetornar401QuandoCredenciaisClienteInvalidas() throws Exception {
        // Cenário: Credenciais de cliente inválidas.
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("cliente@example.com");
        loginRequest.setSenha("senhaIncorretaCliente");

        // Comportamento esperado do mock: authService.validarLoginCliente retorna null.
        when(authService.validarLoginCliente(loginRequest.getEmail(), loginRequest.getSenha())).thenReturn(null);

        // Ação: Realiza uma requisição POST para o endpoint de login de cliente
        mockMvc.perform(post("/api/auth/login-cliente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                // Verificação: Espera um status HTTP 401 Unauthorized.
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$", is(equalTo("Email ou senha inválidos.")))); // Verifica a mensagem de erro.

        // Verifica se authService.validarLoginCliente foi chamado uma vez e jwtUtil nunca foi chamado.
        verify(authService, times(1)).validarLoginCliente(loginRequest.getEmail(), loginRequest.getSenha());
        verifyNoInteractions(jwtUtil); // Não deve gerar token se o login falhar
    }
}