package com.velocompra.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.velocompra.ecommerce.model.Grupo;
import com.velocompra.ecommerce.model.Produto;
import com.velocompra.ecommerce.service.ProdutoService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Classe de testes para {@link ProdutoController}.
 * Utiliza JUnit 5 para os testes e Mockito para simular dependências.
 */
@ExtendWith(MockitoExtension.class)
class ProdutoControllerTest {

    @Mock
    private ProdutoService produtoService;

    @InjectMocks
    private ProdutoController produtoController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(produtoController).build();
        objectMapper = new ObjectMapper();
        // Clear security context before each test to ensure a clean state
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        // Clear security context after each test
        SecurityContextHolder.clearContext();
    }

    // Helper method to set up authentication for a specific role
    private void setupAuthentication(Grupo role) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "user@example.com",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority(role.name()))
        );
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    // 📦 Rotas Públicas (Loja)

    @Test
    @DisplayName("Deve listar produtos ativos com sucesso sem filtro de nome")
    void deveListarProdutosAtivosComSucesso() throws Exception {
        // Arrange
        Produto produto1 = new Produto();
        produto1.setId(1L);
        produto1.setNome("Produto A");
        produto1.setDescricaoDetalhada("Desc A");
        produto1.setPreco(new BigDecimal("100.00"));
        produto1.setQuantidadeEstoque(10);
        produto1.setAtivo(true);
        produto1.setImagens(List.of("imagemA.png", "imagemB.png"));

        Produto produto2 = new Produto();
        produto2.setId(2L);
        produto2.setNome("Produto B");
        produto2.setDescricaoDetalhada("Desc B");
        produto2.setPreco(new BigDecimal("200.00"));
        produto2.setQuantidadeEstoque(20);
        produto2.setAtivo(true);
        produto2.setImagens(List.of("imagemC.png", "imagemD.png"));
        Pageable pageable = PageRequest.of(0, 10);
        Page<Produto> produtosPage = new PageImpl<>(Arrays.asList(produto1, produto2), pageable, 2);

        when(produtoService.listarTodosProdutosAtivos(any(Pageable.class))).thenReturn(produtosPage);

        // Act & Assert
        mockMvc.perform(get("/api/produtos")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nome", is("Produto A")))
                .andExpect(jsonPath("$.content[1].nome", is("Produto B")))
                .andExpect(jsonPath("$.totalElements", is(2)));

        verify(produtoService, times(1)).listarTodosProdutosAtivos(any(Pageable.class));
        verify(produtoService, never()).buscarPorNome(anyString(), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve buscar produtos por nome com sucesso")
    void deveBuscarProdutosPorNomeComSucesso() throws Exception {
        // Arrange
        Produto produto1 = new Produto();
        produto1.setId(1L);
        produto1.setNome("Produto A");
        produto1.setDescricaoDetalhada("Desc A");
        produto1.setPreco(new BigDecimal("100.00"));
        produto1.setQuantidadeEstoque(10);
        produto1.setAtivo(true);
        produto1.setImagens(List.of("imagemA.png", "imagemB.png"));
        Pageable pageable = PageRequest.of(0, 10);
        Page<Produto> produtosPage = new PageImpl<>(Collections.singletonList(produto1), pageable, 1);

        when(produtoService.buscarPorNome(eq("Produto"), any(Pageable.class))).thenReturn(produtosPage);

        // Act & Assert
        mockMvc.perform(get("/api/produtos")
                        .param("nome", "Produto")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nome", is("Produto A")))
                .andExpect(jsonPath("$.totalElements", is(1)));

        verify(produtoService, times(1)).buscarPorNome(eq("Produto"), any(Pageable.class));
        verify(produtoService, never()).listarTodosProdutosAtivos(any(Pageable.class));
    }

    @Test
    @DisplayName("Deve retornar produto por ID com sucesso")
    void deveRetornarProdutoPorIdComSucesso() throws Exception {
        // Arrange
        Long produtoId = 1L;
        Produto produto = new Produto();
        produto.setId(produtoId);
        produto.setNome("Produto A");
        produto.setDescricaoDetalhada("Desc A");
        produto.setPreco(new BigDecimal("100.00"));
        produto.setQuantidadeEstoque(10);
        produto.setAtivo(true);
        produto.setImagens(List.of("imagemA.png", "imagemB.png"));
        when(produtoService.buscarPorId(produtoId)).thenReturn(produto);

        // Act & Assert
        mockMvc.perform(get("/api/produtos/{id}", produtoId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Produto Teste")))
                .andExpect(jsonPath("$.preco", is(50.00)));

        verify(produtoService, times(1)).buscarPorId(produtoId);
    }

    @Test
    @DisplayName("Deve retornar 404 quando produto não encontrado por ID")
    void deveRetornarNotFoundQuandoProdutoNaoEncontradoPorId() throws Exception {
        // Arrange
        Long produtoId = 99L;
        when(produtoService.buscarPorId(produtoId)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/produtos/{id}", produtoId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(produtoService, times(1)).buscarPorId(produtoId);
    }

    // 🔒 Rotas Privadas (Admin e Estoquista)

    @Test
    @DisplayName("ADMIN: Deve listar todos os produtos (ativos e inativos) com sucesso sem filtro de nome")
    void deveListarTodosProdutosAdminComSucesso() throws Exception {
        // Arrange
        setupAuthentication(Grupo.ADMINISTRADOR);
        Produto produto1 = new Produto();
        produto1.setId(1L);
        produto1.setNome("Produto A");
        produto1.setDescricaoDetalhada("Desc A");
        produto1.setPreco(new BigDecimal("100.00"));
        produto1.setQuantidadeEstoque(10);
        produto1.setAtivo(true);
        produto1.setImagens(List.of("imagemA.png", "imagemB.png"));

        Produto produto2 = new Produto();
        produto2.setId(2L);
        produto2.setNome("Produto B");
        produto2.setDescricaoDetalhada("Desc B");
        produto2.setPreco(new BigDecimal("200.00"));
        produto2.setQuantidadeEstoque(20);
        produto2.setAtivo(true);
        produto2.setImagens(List.of("imagemC.png", "imagemD.png"));
        Pageable pageable = PageRequest.of(0, 10);
        Page<Produto> produtosPage = new PageImpl<>(Arrays.asList(produto1, produto2), pageable, 2);

        when(produtoService.listarTodosComInativos(any(Pageable.class))).thenReturn(produtosPage);

        // Act & Assert
        mockMvc.perform(get("/api/produtos/admin")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nome", is("Produto A")))
                .andExpect(jsonPath("$.content[1].nome", is("Produto B (Inativo)")))
                .andExpect(jsonPath("$.totalElements", is(2)));

        verify(produtoService, times(1)).listarTodosComInativos(any(Pageable.class));
        verify(produtoService, never()).buscarPorNomeTodos(anyString(), any(Pageable.class));
    }

    @Test
    @DisplayName("ESTOQUISTA: Deve listar todos os produtos (ativos e inativos) com sucesso com filtro de nome")
    void deveBuscarProdutosAdminPorNomeComSucesso() throws Exception {
        // Arrange
        setupAuthentication(Grupo.ESTOQUISTA);
        Produto produto1 = new Produto();
        produto1.setId(1L);
        produto1.setNome("Produto A");
        produto1.setDescricaoDetalhada("Desc A");
        produto1.setPreco(new BigDecimal("100.00"));
        produto1.setQuantidadeEstoque(10);
        produto1.setAtivo(true);
        produto1.setImagens(List.of("imagemA.png", "imagemB.png"));
        Produto produto2 = new Produto();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Produto> produtosPage = new PageImpl<>(Collections.singletonList(produto1), pageable, 1);

        when(produtoService.buscarPorNomeTodos(eq("Admin"), any(Pageable.class))).thenReturn(produtosPage);

        // Act & Assert
        mockMvc.perform(get("/api/produtos/admin")
                        .param("nome", "Admin")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nome", is("Produto Admin")))
                .andExpect(jsonPath("$.totalElements", is(1)));

        verify(produtoService, times(1)).buscarPorNomeTodos(eq("Admin"), any(Pageable.class));
        verify(produtoService, never()).listarTodosComInativos(any(Pageable.class));
    }

    @Test
    @DisplayName("ADMIN: Deve cadastrar produto com sucesso")
    void deveCadastrarProdutoComSucesso() throws Exception {
        // Arrange
        setupAuthentication(Grupo.ADMINISTRADOR);
        MockMultipartFile imagem = new MockMultipartFile("imagens", "imagem.png", "image/png", "some image data".getBytes());

        doNothing().when(produtoService).cadastrarProduto(
                anyString(), anyString(), any(BigDecimal.class), anyInt(), anyInt(), any(MultipartFile[].class));

        // Act & Assert
        mockMvc.perform(multipart("/api/produtos")
                        .file(imagem)
                        .param("nome", "Novo Produto")
                        .param("descricaoDetalhada", "Detalhes do novo produto")
                        .param("preco", "123.45")
                        .param("quantidadeEstoque", "50")
                        .param("imagemPadrao", "0")
                        .with(request -> {
                            request.setMethod("POST"); // Ensure it's a POST request
                            return request;
                        })
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is("Produto cadastrado com sucesso!")));

        verify(produtoService, times(1)).cadastrarProduto(
                eq("Novo Produto"), eq("Detalhes do novo produto"), eq(new BigDecimal("123.45")), eq(50), eq(0), any(MultipartFile[].class));
    }

    @Test
    @DisplayName("ADMIN: Deve retornar bad request ao cadastrar produto com erro no serviço")
    void deveRetornarBadRequestAoCadastrarProdutoComErro() throws Exception {
        // Arrange
        setupAuthentication(Grupo.ADMINISTRADOR);
        MockMultipartFile imagem = new MockMultipartFile("imagens", "imagem.png", "image/png", "some image data".getBytes());

        doThrow(new RuntimeException("Erro de teste de cadastro")).when(produtoService).cadastrarProduto(
                anyString(), anyString(), any(BigDecimal.class), anyInt(), anyInt(), any(MultipartFile[].class));

        // Act & Assert
        mockMvc.perform(multipart("/api/produtos")
                        .file(imagem)
                        .param("nome", "Produto Com Erro")
                        .param("descricaoDetalhada", "Detalhes")
                        .param("preco", "10.00")
                        .param("quantidadeEstoque", "1")
                        .param("imagemPadrao", "0")
                        .with(request -> {
                            request.setMethod("POST");
                            return request;
                        })
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", is("Erro ao cadastrar produto: Erro de teste de cadastro")));

        verify(produtoService, times(1)).cadastrarProduto(
                eq("Produto Com Erro"), eq("Detalhes"), eq(new BigDecimal("10.00")), eq(1), eq(0), any(MultipartFile[].class));
    }

    @Test
    @DisplayName("ADMIN: Deve editar produto com sucesso")
    void deveEditarProdutoComSucesso() throws Exception {
        // Arrange
        setupAuthentication(Grupo.ADMINISTRADOR);
        Long produtoId = 1L;
        MockMultipartFile imagem = new MockMultipartFile("imagens", "imagem.png", "image/png", "some image data".getBytes());
        Produto produtoAtualizado = new Produto();
        produtoAtualizado.setId(produtoId);
        produtoAtualizado.setNome("Produto Editado");
        produtoAtualizado.setDescricaoDetalhada("Desc Editada");
        produtoAtualizado.setPreco(new BigDecimal("150.00"));
        produtoAtualizado.setQuantidadeEstoque(15);
        produtoAtualizado.setAtivo(true);
        produtoAtualizado.setImagens(List.of("imagem2.png"));
        Produto produto2 = new Produto();

        when(produtoService.editarProduto(
                eq(produtoId), anyString(), anyString(), any(BigDecimal.class), anyInt(), anyInt(), any(MultipartFile[].class)))
                .thenReturn(produtoAtualizado);

        // Act & Assert
        mockMvc.perform(multipart("/api/produtos/{id}", produtoId)
                        .file(imagem)
                        .param("nome", "Produto Editado")
                        .param("descricaoDetalhada", "Desc Editada")
                        .param("preco", "150.00")
                        .param("quantidadeEstoque", "15")
                        .param("imagemPadrao", "0")
                        .with(request -> {
                            request.setMethod("PUT"); // Ensure it's a PUT request
                            return request;
                        })
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Produto Editado")))
                .andExpect(jsonPath("$.preco", is(150.00)));

        verify(produtoService, times(1)).editarProduto(
                eq(produtoId), eq("Produto Editado"), eq("Desc Editada"), eq(new BigDecimal("150.00")), eq(15), eq(0), any(MultipartFile[].class));
    }

    @Test
    @DisplayName("ADMIN: Deve retornar bad request ao editar produto com erro no serviço")
    void deveRetornarBadRequestAoEditarProdutoComErro() throws Exception {
        // Arrange
        setupAuthentication(Grupo.ADMINISTRADOR);
        Long produtoId = 1L;
        MockMultipartFile imagem = new MockMultipartFile("imagens", "imagem.png", "image/png", "some image data".getBytes());

        doThrow(new RuntimeException("Erro de teste de edição")).when(produtoService).editarProduto(
                eq(produtoId), anyString(), anyString(), any(BigDecimal.class), anyInt(), anyInt(), any(MultipartFile[].class));

        // Act & Assert
        mockMvc.perform(multipart("/api/produtos/{id}", produtoId)
                        .file(imagem)
                        .param("nome", "Produto Editado")
                        .param("descricaoDetalhada", "Desc Editada")
                        .param("preco", "150.00")
                        .param("quantidadeEstoque", "15")
                        .param("imagemPadrao", "0")
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", is("Erro ao editar produto: Erro de teste de edição")));

        verify(produtoService, times(1)).editarProduto(
                eq(produtoId), eq("Produto Editado"), eq("Desc Editada"), eq(new BigDecimal("150.00")), eq(15), eq(0), any(MultipartFile[].class));
    }

    @Test
    @DisplayName("ADMIN: Deve habilitar/inabilitar produto com sucesso")
    void deveHabilitarInabilitarProdutoComSucesso() throws Exception {
        // Arrange
        setupAuthentication(Grupo.ADMINISTRADOR);
        Long produtoId = 1L;
        doNothing().when(produtoService).habilitarInabilitar(produtoId);

        // Act & Assert
        mockMvc.perform(patch("/api/produtos/{id}/status", produtoId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(produtoService, times(1)).habilitarInabilitar(produtoId);
    }

    @Test
    @DisplayName("ESTOQUISTA: Deve alterar estoque de produto com sucesso")
    void deveAlterarEstoqueComSucesso() throws Exception {
        // Arrange
        setupAuthentication(Grupo.ESTOQUISTA);
        Long produtoId = 1L;
        int novaQuantidade = 25;
        doNothing().when(produtoService).alterarQuantidadeEstoque(produtoId, novaQuantidade);

        // Act & Assert
        mockMvc.perform(patch("/api/produtos/{id}/estoque", produtoId)
                        .param("quantidadeEstoque", String.valueOf(novaQuantidade))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is("Estoque atualizado com sucesso")));

        verify(produtoService, times(1)).alterarQuantidadeEstoque(produtoId, novaQuantidade);
    }

    @Test
    @DisplayName("ESTOQUISTA: Deve retornar bad request ao alterar estoque com erro no serviço")
    void deveRetornarBadRequestAoAlterarEstoqueComErro() throws Exception {
        // Arrange
        setupAuthentication(Grupo.ESTOQUISTA);
        Long produtoId = 1L;
        int novaQuantidade = 25;
        doThrow(new RuntimeException("Erro ao buscar produto para estoque")).when(produtoService).alterarQuantidadeEstoque(produtoId, novaQuantidade);

        // Act & Assert
        mockMvc.perform(patch("/api/produtos/{id}/estoque", produtoId)
                        .param("quantidadeEstoque", String.valueOf(novaQuantidade))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", is("Erro ao atualizar estoque: Erro ao buscar produto para estoque")));

        verify(produtoService, times(1)).alterarQuantidadeEstoque(produtoId, novaQuantidade);
    }

    // Security Tests (Unauthorized Access)

    @Test
    @DisplayName("CLIENTE: Deve retornar 403 Forbidden ao tentar listar produtos admin")
    void clienteNaoDeveListarProdutosAdmin() throws Exception {
        // Arrange
        setupAuthentication(Grupo.CLIENTE);

        // Act & Assert
        mockMvc.perform(get("/api/produtos/admin")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden()); // Expect 403 Forbidden
    }

    @Test
    @DisplayName("CLIENTE: Deve retornar 403 Forbidden ao tentar cadastrar produto")
    void clienteNaoDeveCadastrarProduto() throws Exception {
        // Arrange
        setupAuthentication(Grupo.CLIENTE);
        MockMultipartFile imagem = new MockMultipartFile("imagens", "imagem.png", "image/png", "some image data".getBytes());

        // Act & Assert
        mockMvc.perform(multipart("/api/produtos")
                        .file(imagem)
                        .param("nome", "Novo Produto")
                        .param("descricaoDetalhada", "Detalhes do novo produto")
                        .param("preco", "123.45")
                        .param("quantidadeEstoque", "50")
                        .param("imagemPadrao", "0")
                        .with(request -> {
                            request.setMethod("POST");
                            return request;
                        })
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("CLIENTE: Deve retornar 403 Forbidden ao tentar editar produto")
    void clienteNaoDeveEditarProduto() throws Exception {
        // Arrange
        setupAuthentication(Grupo.CLIENTE);
        Long produtoId = 1L;
        MockMultipartFile imagem = new MockMultipartFile("imagens", "imagem.png", "image/png", "some image data".getBytes());

        // Act & Assert
        mockMvc.perform(multipart("/api/produtos/{id}", produtoId)
                        .file(imagem)
                        .param("nome", "Produto Editado")
                        .param("descricaoDetalhada", "Desc Editada")
                        .param("preco", "150.00")
                        .param("quantidadeEstoque", "15")
                        .param("imagemPadrao", "0")
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("ESTOQUISTA: Deve retornar 403 Forbidden ao tentar habilitar/inabilitar produto")
    void estoquistaNaoDeveHabilitarInabilitarProduto() throws Exception {
        // Arrange
        setupAuthentication(Grupo.ESTOQUISTA);
        Long produtoId = 1L;

        // Act & Assert
        mockMvc.perform(patch("/api/produtos/{id}/status", produtoId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("ADMIN: Deve retornar 403 Forbidden ao tentar alterar estoque")
    void adminNaoDeveAlterarEstoque() throws Exception {
        // Arrange
        setupAuthentication(Grupo.ADMINISTRADOR);
        Long produtoId = 1L;
        int novaQuantidade = 25;

        // Act & Assert
        mockMvc.perform(patch("/api/produtos/{id}/estoque", produtoId)
                        .param("quantidadeEstoque", String.valueOf(novaQuantidade))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}