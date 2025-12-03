package com.velocompra.ecommerce.controller; // Declaração do pacote onde a classe de teste está localizada.

// Importações de classes e interfaces necessárias para o teste
import com.fasterxml.jackson.databind.ObjectMapper; // Usado para serialização/deserialização de objetos JSON.
import com.velocompra.ecommerce.config.SecurityConfig; // Importa a classe de configuração de segurança do Spring.
import com.velocompra.ecommerce.model.Grupo; // Importa o enum Grupo que define os papéis de usuário.
import com.velocompra.ecommerce.model.Produto; // Importa a classe de modelo Produto.
import com.velocompra.ecommerce.service.ProdutoService; // Importa a interface/classe de serviço ProdutoService.
import org.junit.jupiter.api.AfterEach; // Anotação JUnit 5 para métodos executados após cada teste.
import org.junit.jupiter.api.BeforeEach; // Anotação JUnit 5 para métodos executados antes de cada teste.
import org.junit.jupiter.api.DisplayName; // Anotação JUnit 5 para definir um nome legível para o teste.
import org.junit.jupiter.api.Test; // Anotação JUnit 5 para marcar um método como um teste.
import org.junit.jupiter.api.extension.ExtendWith; // Anotação JUnit 5 para estender o comportamento de teste com extensões.
import org.mockito.junit.jupiter.MockitoExtension; // Extensão JUnit 5 para integrar o Mockito.
import org.springframework.beans.factory.annotation.Autowired; // Anotação Spring para injeção automática de dependências.
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest; // Anotação Spring Boot para testes focados na camada web MVC.
import org.springframework.context.annotation.Import; // Anotação Spring para importar classes de configuração.
import org.springframework.data.domain.Page; // Interface do Spring Data para representação de páginas de dados.
import org.springframework.data.domain.PageImpl; // Implementação concreta de Page.
import org.springframework.data.domain.PageRequest; // Classe para criar objetos Pageable.
import org.springframework.data.domain.Pageable; // Interface para informações de paginação.
import org.springframework.http.MediaType; // Contém tipos de mídia comuns (ex: application/json).
import org.springframework.mock.web.MockMultipartFile; // Classe mock do Spring para simular upload de arquivos.
import org.springframework.security.core.authority.SimpleGrantedAuthority; // Implementação simples de GrantedAuthority.
import org.springframework.security.core.context.SecurityContextHolder; // Gerencia o SecurityContext para o thread atual.
import org.springframework.test.context.bean.override.mockito.MockitoBean; // Anotação Spring para criar e injetar um mockito mock como um bean Spring.
import org.springframework.test.web.servlet.MockMvc; // Objeto principal para testar controladores Spring MVC.
import org.springframework.test.web.servlet.setup.MockMvcBuilders; // Construtor para configurar instâncias de MockMvc.
import org.springframework.web.context.WebApplicationContext; // Contexto de aplicação web do Spring.
import org.springframework.web.multipart.MultipartFile; // Interface para representar um arquivo enviado em requisições multipart.

import java.math.BigDecimal; // Classe para manipulação de números decimais com precisão.
import java.util.Arrays; // Classe utilitária para manipulação de arrays.
import java.util.Collections; // Classe utilitária para operações em coleções.
import java.util.List; // Interface para coleções ordenadas.

// Importações estáticas para simplificar o código de teste (Mockito, Spring Security Test, MockMvcBuilders)
import static org.hamcrest.Matchers.is; // Asserção Hamcrest para verificar igualdade.
import static org.mockito.ArgumentMatchers.any; // Argument matcher do Mockito para qualquer objeto de um tipo.
import static org.mockito.ArgumentMatchers.anyInt; // Argument matcher do Mockito para qualquer int.
import static org.mockito.ArgumentMatchers.anyString; // Argument matcher do Mockito para qualquer String.
import static org.mockito.ArgumentMatchers.eq; // Argument matcher do Mockito para argumentos exatos.
import static org.mockito.Mockito.*; // Importa todos os métodos estáticos do Mockito (e.g., when, verify, times, never).
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf; // Post-processador para adicionar token CSRF.
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user; // Post-processador para simular um usuário autenticado.
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity; // Configuração para aplicar segurança Spring ao MockMvc.
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get; // Construtor de requisições GET.
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart; // Construtor de requisições multipart (para upload de arquivos).
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch; // Construtor de requisições PATCH.
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post; // Construtor de requisições POST.
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put; // Construtor de requisições PUT.
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath; // Verifica valores em JSON retornado.
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status; // Verifica o status HTTP da resposta.

/**
 * Classe de testes para {@link ProdutoController}.
 * Utiliza JUnit 5 para os testes e Mockito para simular dependências.
 * Anotada com {@code @WebMvcTest} para focar no teste da camada web.
 * Inclui a {@code SecurityConfig} para garantir que as configurações de segurança sejam aplicadas.
 */
@WebMvcTest(ProdutoController.class) // Indica que esta é uma fatia de teste focada no ProdutoController, carregando apenas beans relacionados à camada web.
@ExtendWith(MockitoExtension.class) // Habilita a integração do Mockito com o JUnit 5.
@Import(SecurityConfig.class) // Importa a classe de configuração de segurança para que @EnableMethodSecurity e HttpSecurity sejam processados.
class ProdutoControllerTest {

    /**
     * Injeta um mock do {@link ProdutoService} no contexto do Spring para isolar o teste do controller.
     * {@code @MockitoBean} é preferível a {@code @Mock} em testes {@code @WebMvcTest} para que o mock seja um bean gerenciado pelo Spring.
     */
    @MockitoBean
    private ProdutoService produtoService;

    /**
     * Objeto MockMvc injetado automaticamente pelo Spring.
     * Usado para realizar chamadas HTTP simuladas ao controller.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * Contexto da aplicação web injetado automaticamente pelo Spring.
     * Necessário para configurar o MockMvc com a segurança Spring.
     */
    @Autowired
    private WebApplicationContext webApplicationContext;

    /**
     * Objeto ObjectMapper para serializar e desserializar JSON, útil em alguns cenários de teste.
     */
    private ObjectMapper objectMapper;

    /**
     * Configuração inicial executada antes de cada método de teste.
     * Configura o MockMvc com a segurança do Spring e limpa o contexto de segurança.
     */
    @BeforeEach
    void setUp() {
        // Reconfigura o MockMvc para incluir a segurança Spring e desabilitar CSRF para testes.
        // `webAppContextSetup` configura o MockMvc com o contexto da aplicação web.
        // `apply(springSecurity())` aplica as configurações de segurança do Spring ao MockMvc.
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        objectMapper = new ObjectMapper(); // Inicializa o ObjectMapper.
        SecurityContextHolder.clearContext(); // Limpa o contexto de segurança do Spring antes de cada teste para evitar poluição de estado entre testes.
    }

    /**
     * Método executado após cada método de teste.
     * Garante que o contexto de segurança esteja limpo para o próximo teste.
     */
    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext(); // Limpa o contexto de segurança do Spring após cada teste.
    }

    /**
     * Método auxiliar para criar um {@link org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor}
     * que simula um usuário com um determinado papel (autoridade).
     *
     * @param role O {@link Grupo} que representa o papel do usuário.
     * @return Um UserRequestPostProcessor configurado para autenticação.
     */
    private org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor withRole(Grupo role) {
        // Cria um PostProcessor de usuário com um nome de usuário, senha e a autoridade baseada no Grupo fornecido.
        return user("testuser").password("password").authorities(new SimpleGrantedAuthority(role.name()));
    }

    // --- Rotas Públicas (Loja) ---

    /**
     * Testa se a listagem de produtos ativos sem filtro de nome retorna sucesso (status 200 OK)
     * e os dados esperados.
     *
     * @throws Exception Se ocorrer um erro durante a execução da requisição MockMvc.
     */
    @Test
    @DisplayName("Deve listar produtos ativos com sucesso sem filtro de nome")
    void deveListarProdutosAtivosComSucesso() throws Exception {
        // Arrange (Configuração dos dados de entrada e mocks)
        Produto produto1 = new Produto(); // Cria uma instância de Produto para o mock.
        produto1.setId(1L); // Define o ID do produto.
        produto1.setNome("Produto A"); // Define o nome.
        produto1.setDescricaoDetalhada("Desc A"); // Define a descrição.
        produto1.setPreco(new BigDecimal("100.00")); // Define o preço.
        produto1.setQuantidadeEstoque(10); // Define a quantidade em estoque.
        produto1.setAtivo(true); // Marca o produto como ativo.
        produto1.setImagens(List.of("imagemA.png", "imagemB.png")); // Define as imagens.

        Produto produto2 = new Produto(); // Cria uma segunda instância de Produto para o mock.
        produto2.setId(2L); // Define o ID.
        produto2.setNome("Produto B"); // Define o nome.
        produto2.setDescricaoDetalhada("Desc B"); // Define a descrição.
        produto2.setPreco(new BigDecimal("200.00")); // Define o preço.
        produto2.setQuantidadeEstoque(20); // Define a quantidade em estoque.
        produto2.setAtivo(true); // Marca como ativo.
        produto2.setImagens(List.of("imagemC.png", "imagemD.png")); // Define as imagens.

        Pageable pageable = PageRequest.of(0, 10); // Cria um objeto Pageable para a paginação (página 0, tamanho 10).
        Page<Produto> produtosPage = new PageImpl<>(Arrays.asList(produto1, produto2), pageable, 2); // Cria uma página de produtos simulada.

        // Configura o mock do produtoService: quando 'listarTodosProdutosAtivos' for chamado com qualquer Pageable, retorne 'produtosPage'.
        when(produtoService.listarTodosProdutosAtivos(any(Pageable.class))).thenReturn(produtosPage);

        // Act & Assert (Execução da requisição e verificação dos resultados)
        mockMvc.perform(get("/api/produtos") // Realiza uma requisição GET para "/api/produtos".
                        .param("page", "0") // Adiciona o parâmetro de página.
                        .param("size", "10") // Adiciona o parâmetro de tamanho.
                        .contentType(MediaType.APPLICATION_JSON)) // Define o tipo de conteúdo da requisição como JSON.
                .andExpect(status().isOk()) // Espera que o status da resposta seja 200 OK.
                .andExpect(jsonPath("$.content[0].nome", is("Produto A"))) // Verifica o nome do primeiro produto no JSON.
                .andExpect(jsonPath("$.content[1].nome", is("Produto B"))) // Verifica o nome do segundo produto no JSON.
                .andExpect(jsonPath("$.totalElements", is(2))); // Verifica o total de elementos na página.

        // Verifica se o método 'listarTodosProdutosAtivos' do produtoService foi chamado exatamente uma vez.
        verify(produtoService, times(1)).listarTodosProdutosAtivos(any(Pageable.class));
        // Verifica se o método 'buscarPorNome' do produtoService nunca foi chamado.
        verify(produtoService, never()).buscarPorNome(anyString(), any(Pageable.class));
    }

    /**
     * Testa se a busca de produtos por nome retorna sucesso (status 200 OK)
     * e os dados esperados.
     *
     * @throws Exception Se ocorrer um erro durante a execução da requisição MockMvc.
     */
    @Test
    @DisplayName("Deve buscar produtos por nome com sucesso")
    void deveBuscarProdutosPorNomeComSucesso() throws Exception {
        // Arrange
        Produto produto1 = new Produto(); // Cria uma instância de Produto.
        produto1.setId(1L); // Define o ID.
        produto1.setNome("Produto A"); // Define o nome.
        produto1.setDescricaoDetalhada("Desc A"); // Define a descrição.
        produto1.setPreco(new BigDecimal("100.00")); // Define o preço.
        produto1.setQuantidadeEstoque(10); // Define a quantidade.
        produto1.setAtivo(true); // Marca como ativo.
        produto1.setImagens(List.of("imagemA.png", "imagemB.png")); // Define as imagens.
        Pageable pageable = PageRequest.of(0, 10); // Cria um Pageable.
        Page<Produto> produtosPage = new PageImpl<>(Collections.singletonList(produto1), pageable, 1); // Cria uma página com um único produto.

        // Configura o mock: quando 'buscarPorNome' for chamado com "Produto" e qualquer Pageable, retorne 'produtosPage'.
        when(produtoService.buscarPorNome(eq("Produto"), any(Pageable.class))).thenReturn(produtosPage);

        // Act & Assert
        mockMvc.perform(get("/api/produtos") // Realiza requisição GET.
                        .param("nome", "Produto") // Adiciona o parâmetro de nome.
                        .param("page", "0") // Adiciona o parâmetro de página.
                        .param("size", "10") // Adiciona o parâmetro de tamanho.
                        .contentType(MediaType.APPLICATION_JSON)) // Define o tipo de conteúdo.
                .andExpect(status().isOk()) // Espera status 200 OK.
                .andExpect(jsonPath("$.content[0].nome", is("Produto A"))) // Verifica o nome.
                .andExpect(jsonPath("$.totalElements", is(1))); // Verifica o total de elementos.

        // Verifica se 'buscarPorNome' foi chamado uma vez com os argumentos corretos.
        verify(produtoService, times(1)).buscarPorNome(eq("Produto"), any(Pageable.class));
        // Verifica se 'listarTodosProdutosAtivos' nunca foi chamado.
        verify(produtoService, never()).listarTodosProdutosAtivos(any(Pageable.class));
    }

    /**
     * Testa se a busca de um produto por ID retorna sucesso (status 200 OK)
     * e os dados do produto.
     *
     * @throws Exception Se ocorrer um erro durante a execução da requisição MockMvc.
     */
    @Test
    @DisplayName("Deve retornar produto por ID com sucesso")
    void deveRetornarProdutoPorIdComSucesso() throws Exception {
        // Arrange
        Long produtoId = 1L; // Define um ID de produto.
        Produto produto = new Produto(); // Cria uma instância de Produto.
        produto.setId(produtoId); // Define o ID do produto.
        produto.setNome("Produto A"); // Define o nome.
        produto.setDescricaoDetalhada("Desc A"); // Define a descrição.
        produto.setPreco(new BigDecimal("100.00")); // Define o preço.
        produto.setQuantidadeEstoque(10); // Define a quantidade.
        produto.setAtivo(true); // Marca como ativo.
        produto.setImagens(List.of("imagemA.png", "imagemB.png")); // Define as imagens.
        // Configura o mock: quando 'buscarPorId' for chamado com o ID específico, retorne o produto mockado.
        when(produtoService.buscarPorId(produtoId)).thenReturn(produto);

        // Act & Assert
        mockMvc.perform(get("/api/produtos/{id}", produtoId) // Realiza requisição GET para o ID específico.
                        .contentType(MediaType.APPLICATION_JSON)) // Define o tipo de conteúdo.
                .andExpect(status().isOk()) // Espera status 200 OK.
                .andExpect(jsonPath("$.nome", is("Produto A"))) // Verifica o nome no JSON.
                .andExpect(jsonPath("$.preco", is(100.00))); // Verifica o preço no JSON.

        // Verifica se 'buscarPorId' foi chamado uma vez com o ID correto.
        verify(produtoService, times(1)).buscarPorId(produtoId);
    }

    /**
     * Testa se a busca de um produto por ID que não existe retorna 404 Not Found.
     *
     * @throws Exception Se ocorrer um erro durante a execução da requisição MockMvc.
     */
    @Test
    @DisplayName("Deve retornar 404 quando produto não encontrado por ID")
    void deveRetornarNotFoundQuandoProdutoNaoEncontradoPorId() throws Exception {
        // Arrange
        Long produtoId = 99L; // Define um ID que não deve existir.
        // Configura o mock: quando 'buscarPorId' for chamado com o ID específico, retorne null (produto não encontrado).
        when(produtoService.buscarPorId(produtoId)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/produtos/{id}", produtoId) // Realiza requisição GET.
                        .contentType(MediaType.APPLICATION_JSON)) // Define o tipo de conteúdo.
                .andExpect(status().isNotFound()); // Espera status 404 Not Found.

        // Verifica se 'buscarPorId' foi chamado uma vez com o ID correto.
        verify(produtoService, times(1)).buscarPorId(produtoId);
    }

    // --- Rotas Privadas (Admin e Estoquista) ---

    /**
     * Testa se um usuário ADMINISTRADOR pode listar todos os produtos (ativos e inativos) com sucesso.
     *
     * @throws Exception Se ocorrer um erro durante a execução da requisição MockMvc.
     */
    @Test
    @DisplayName("ADMIN: Deve listar todos os produtos (ativos e inativos) com sucesso sem filtro de nome")
    void deveListarTodosProdutosAdminComSucesso() throws Exception {
        // Arrange
        Produto produto1 = new Produto(); // Cria um produto ativo.
        produto1.setId(1L);
        produto1.setNome("Produto A");
        produto1.setDescricaoDetalhada("Desc A");
        produto1.setPreco(new BigDecimal("100.00"));
        produto1.setQuantidadeEstoque(10);
        produto1.setAtivo(true);
        produto1.setImagens(List.of("imagemA.png", "imagemB.png"));

        Produto produto2 = new Produto(); // Cria um produto inativo.
        produto2.setId(2L);
        produto2.setNome("Produto B");
        produto2.setDescricaoDetalhada("Desc B");
        produto2.setPreco(new BigDecimal("200.00"));
        produto2.setQuantidadeEstoque(20);
        produto2.setAtivo(false);
        produto2.setImagens(List.of("imagemC.png", "imagemD.png"));
        Pageable pageable = PageRequest.of(0, 10); // Cria um Pageable.
        Page<Produto> produtosPage = new PageImpl<>(Arrays.asList(produto1, produto2), pageable, 2); // Cria a página de produtos.

        // Configura o mock: quando 'listarTodosComInativos' for chamado, retorne a página de produtos.
        when(produtoService.listarTodosComInativos(any(Pageable.class))).thenReturn(produtosPage);

        // Act & Assert
        mockMvc.perform(get("/api/produtos/admin") // Realiza requisição GET para a rota de admin.
                        .param("page", "0") // Parâmetro de página.
                        .param("size", "10") // Parâmetro de tamanho.
                        .contentType(MediaType.APPLICATION_JSON) // Tipo de conteúdo.
                        .with(withRole(Grupo.ADMINISTRADOR))) // Autentica a requisição como um usuário ADMINISTRADOR.
                .andExpect(status().isOk()) // Espera status 200 OK.
                .andExpect(jsonPath("$.content[0].nome", is("Produto A"))) // Verifica o nome do primeiro produto.
                .andExpect(jsonPath("$.content[1].nome", is("Produto B"))) // Verifica o nome do segundo produto.
                .andExpect(jsonPath("$.totalElements", is(2))); // Verifica o total de elementos.

        // Verifica se 'listarTodosComInativos' foi chamado uma vez.
        verify(produtoService, times(1)).listarTodosComInativos(any(Pageable.class));
        // Verifica se 'buscarPorNomeTodos' nunca foi chamado.
        verify(produtoService, never()).buscarPorNomeTodos(anyString(), any(Pageable.class));
    }

    /**
     * Testa se um usuário ESTOQUISTA pode listar todos os produtos (ativos e inativos)
     * com sucesso usando um filtro de nome.
     *
     * @throws Exception Se ocorrer um erro durante a execução da requisição MockMvc.
     */
    @Test
    @DisplayName("ESTOQUISTA: Deve listar todos os produtos (ativos e inativos) com sucesso com filtro de nome")
    void deveBuscarProdutosAdminPorNomeComSucesso() throws Exception {
        // Arrange
        Produto produto1 = new Produto(); // Cria um produto.
        produto1.setId(1L);
        produto1.setNome("Produto Admin");
        produto1.setDescricaoDetalhada("Desc A");
        produto1.setPreco(new BigDecimal("100.00"));
        produto1.setQuantidadeEstoque(10);
        produto1.setAtivo(true);
        produto1.setImagens(List.of("imagemA.png", "imagemB.png"));

        Pageable pageable = PageRequest.of(0, 10); // Cria um Pageable.
        Page<Produto> produtosPage = new PageImpl<>(Collections.singletonList(produto1), pageable, 1); // Cria a página.

        // Configura o mock: quando 'buscarPorNomeTodos' for chamado com "Admin" e qualquer Pageable, retorne a página.
        when(produtoService.buscarPorNomeTodos(eq("Admin"), any(Pageable.class))).thenReturn(produtosPage);

        // Act & Assert
        mockMvc.perform(get("/api/produtos/admin") // Realiza requisição GET para a rota de admin.
                        .param("nome", "Admin") // Parâmetro de nome.
                        .param("page", "0") // Parâmetro de página.
                        .param("size", "10") // Parâmetro de tamanho.
                        .contentType(MediaType.APPLICATION_JSON) // Tipo de conteúdo.
                        .with(withRole(Grupo.ESTOQUISTA))) // Autentica a requisição como um usuário ESTOQUISTA.
                .andExpect(status().isOk()) // Espera status 200 OK.
                .andExpect(jsonPath("$.content[0].nome", is("Produto Admin"))) // Verifica o nome do produto.
                .andExpect(jsonPath("$.totalElements", is(1))); // Verifica o total de elementos.

        // Verifica se 'buscarPorNomeTodos' foi chamado uma vez.
        verify(produtoService, times(1)).buscarPorNomeTodos(eq("Admin"), any(Pageable.class));
        // Verifica se 'listarTodosComInativos' nunca foi chamado.
        verify(produtoService, never()).listarTodosComInativos(any(Pageable.class));
    }

    /**
     * Testa se um usuário ADMINISTRADOR pode cadastrar um produto com sucesso.
     *
     * @throws Exception Se ocorrer um erro durante a execução da requisição MockMvc.
     */
    @Test
    @DisplayName("ADMIN: Deve cadastrar produto com sucesso")
    void deveCadastrarProdutoComSucesso() throws Exception {
        // Arrange
        MockMultipartFile imagem = new MockMultipartFile("imagens", "imagem.png", "image/png", "some image data".getBytes()); // Cria um arquivo mock de imagem.

        // Crie um objeto Produto de retorno para o mock do service.
        Produto produtoRetornado = new Produto(); // Instância de Produto a ser retornada pelo mock do service.
        produtoRetornado.setId(1L); // Simula um ID para o produto cadastrado.
        produtoRetornado.setNome("Novo Produto"); // Define o nome.
        produtoRetornado.setDescricaoDetalhada("Detalhes do novo produto"); // Define a descrição.
        produtoRetornado.setPreco(new BigDecimal("123.45")); // Define o preço.
        produtoRetornado.setQuantidadeEstoque(50); // Define a quantidade em estoque.
        produtoRetornado.setAtivo(true); // Define como ativo.
        produtoRetornado.setImagens(List.of("imagem.png")); // Define a lista de imagens salvas.
        produtoRetornado.setImagemPadrao("imagem.png"); // Define a imagem padrão.

        // Configura o mock do produtoService: quando 'cadastrarProduto' for chamado com quaisquer argumentos, retorne 'produtoRetornado'.
        when(produtoService.cadastrarProduto(
                anyString(), anyString(), any(BigDecimal.class), anyInt(), anyInt(), any(MultipartFile[].class)))
                .thenReturn(produtoRetornado); // Usa `thenReturn` pois o método do service retorna um `Produto`.

        // Act & Assert
        mockMvc.perform(multipart("/api/produtos") // Realiza uma requisição multipart POST para "/api/produtos".
                        .file(imagem) // Anexa o arquivo mock de imagem.
                        .param("nome", "Novo Produto") // Adiciona o parâmetro de nome.
                        .param("descricaoDetalhada", "Detalhes do novo produto") // Adiciona a descrição.
                        .param("preco", "123.45") // Adiciona o preço.
                        .param("quantidadeEstoque", "50") // Adiciona a quantidade.
                        .param("imagemPadrao", "0") // Adiciona o índice da imagem padrão.
                        .with(request -> { // Configura a requisição para ser um POST explícito.
                            request.setMethod("POST");
                            return request;
                        })
                        .with(withRole(Grupo.ADMINISTRADOR)) // Autentica a requisição como um usuário ADMINISTRADOR.
                        .with(csrf()) // Adiciona o token CSRF para passar pelos filtros de segurança.
                )
                .andExpect(status().isOk()) // Espera que o status da resposta seja 200 OK.
                .andExpect(jsonPath("$", is("Produto cadastrado com sucesso!"))); // Verifica a mensagem de sucesso retornada pelo controller.

        // Verifica se o método 'cadastrarProduto' do produtoService foi chamado exatamente uma vez
        // com os argumentos esperados (usando `eq` para valores específicos e `any` para tipos).
        verify(produtoService, times(1)).cadastrarProduto(
                eq("Novo Produto"), eq("Detalhes do novo produto"), eq(new BigDecimal("123.45")), eq(50), eq(0), any(MultipartFile[].class));
    }

    /**
     * Testa se o cadastro de produto retorna 400 Bad Request quando o serviço lança uma exceção.
     *
     * @throws Exception Se ocorrer um erro durante a execução da requisição MockMvc.
     */
    @Test
    @DisplayName("ADMIN: Deve retornar bad request ao cadastrar produto com erro no serviço")
    void deveRetornarBadRequestAoCadastrarProdutoComErro() throws Exception {
        // Arrange
        MockMultipartFile imagem = new MockMultipartFile("imagens", "imagem.png", "image/png", "some image data".getBytes()); // Cria arquivo mock.

        // Configura o mock do produtoService: quando 'cadastrarProduto' for chamado, lance uma RuntimeException.
        doThrow(new RuntimeException("Erro de teste de cadastro")).when(produtoService).cadastrarProduto(
                anyString(), anyString(), any(BigDecimal.class), anyInt(), anyInt(), any(MultipartFile[].class));

        // Act & Assert
        mockMvc.perform(multipart("/api/produtos") // Realiza requisição multipart POST.
                        .file(imagem) // Anexa o arquivo.
                        .param("nome", "Produto Com Erro") // Parâmetros da requisição.
                        .param("descricaoDetalhada", "Detalhes")
                        .param("preco", "10.00")
                        .param("quantidadeEstoque", "1")
                        .param("imagemPadrao", "0")
                        .with(request -> { // Garante que é um POST.
                            request.setMethod("POST");
                            return request;
                        })
                        .with(withRole(Grupo.ADMINISTRADOR)) // Autentica como ADMIN.
                        .with(csrf()) // Adiciona o token CSRF.
                )
                .andExpect(status().isBadRequest()) // Espera status 400 Bad Request.
                .andExpect(jsonPath("$", is("Erro ao cadastrar produto: Erro de teste de cadastro"))); // Verifica a mensagem de erro.

        // Verifica se 'cadastrarProduto' foi chamado uma vez com os argumentos esperados.
        verify(produtoService, times(1)).cadastrarProduto(
                eq("Produto Com Erro"), eq("Detalhes"), eq(new BigDecimal("10.00")), eq(1), eq(0), any(MultipartFile[].class));
    }

    /**
     * Testa se um usuário ADMINISTRADOR pode editar um produto com sucesso.
     *
     * @throws Exception Se ocorrer um erro durante a execução da requisição MockMvc.
     */
    @Test
    @DisplayName("ADMIN: Deve editar produto com sucesso")
    void deveEditarProdutoComSucesso() throws Exception {
        // Arrange
        Long produtoId = 1L; // Define o ID do produto a ser editado.
        MockMultipartFile imagem = new MockMultipartFile("imagens", "imagem.png", "image/png", "some image data".getBytes()); // Cria arquivo mock.
        Produto produtoAtualizado = new Produto(); // Cria um objeto Produto simulando o retorno do serviço.
        produtoAtualizado.setId(produtoId);
        produtoAtualizado.setNome("Produto Editado");
        produtoAtualizado.setDescricaoDetalhada("Desc Editada");
        produtoAtualizado.setPreco(new BigDecimal("150.00"));
        produtoAtualizado.setQuantidadeEstoque(15);
        produtoAtualizado.setAtivo(true);
        produtoAtualizado.setImagens(List.of("imagem2.png"));

        // Configura o mock: quando 'editarProduto' for chamado, retorne o produto atualizado.
        when(produtoService.editarProduto(
                eq(produtoId), anyString(), anyString(), any(BigDecimal.class), anyInt(), anyInt(), any(MultipartFile[].class)))
                .thenReturn(produtoAtualizado);

        // Act & Assert
        mockMvc.perform(multipart("/api/produtos/{id}", produtoId) // Realiza requisição multipart PUT para o ID.
                        .file(imagem) // Anexa o arquivo.
                        .param("nome", "Produto Editado") // Parâmetros da requisição.
                        .param("descricaoDetalhada", "Desc Editada")
                        .param("preco", "150.00")
                        .param("quantidadeEstoque", "15")
                        .param("imagemPadrao", "0")
                        .with(request -> { // Garante que é um PUT.
                            request.setMethod("PUT");
                            return request;
                        })
                        .with(withRole(Grupo.ADMINISTRADOR)) // Autentica como ADMIN.
                        .with(csrf()) // Adiciona o token CSRF.
                )
                .andExpect(status().isOk()) // Espera status 200 OK.
                .andExpect(jsonPath("$.nome", is("Produto Editado"))) // Verifica o nome no JSON de retorno.
                .andExpect(jsonPath("$.preco", is(150.00))); // Verifica o preço no JSON de retorno.

        // Verifica se 'editarProduto' foi chamado uma vez com os argumentos esperados.
        verify(produtoService, times(1)).editarProduto(
                eq(produtoId), eq("Produto Editado"), eq("Desc Editada"), eq(new BigDecimal("150.00")), eq(15), eq(0), any(MultipartFile[].class));
    }

    /**
     * Testa se a edição de produto retorna 400 Bad Request quando o serviço lança uma exceção.
     *
     * @throws Exception Se ocorrer um erro durante a execução da requisição MockMvc.
     */
    @Test
    @DisplayName("ADMIN: Deve retornar bad request ao editar produto com erro no serviço")
    void deveRetornarBadRequestAoEditarProdutoComErro() throws Exception {
        // Arrange
        Long produtoId = 1L; // Define o ID do produto.
        MockMultipartFile imagem = new MockMultipartFile("imagens", "imagem.png", "image/png", "some image data".getBytes()); // Cria arquivo mock.

        // Configura o mock: quando 'editarProduto' for chamado, lance uma RuntimeException.
        doThrow(new RuntimeException("Erro de teste de edição")).when(produtoService).editarProduto(
                eq(produtoId), anyString(), anyString(), any(BigDecimal.class), anyInt(), anyInt(), any(MultipartFile[].class));

        // Act & Assert
        mockMvc.perform(multipart("/api/produtos/{id}", produtoId) // Realiza requisição multipart PUT.
                        .file(imagem) // Anexa o arquivo.
                        .param("nome", "Produto Editado") // Parâmetros da requisição.
                        .param("descricaoDetalhada", "Desc Editada")
                        .param("preco", "150.00")
                        .param("quantidadeEstoque", "15")
                        .param("imagemPadrao", "0")
                        .with(request -> { // Garante que é um PUT.
                            request.setMethod("PUT");
                            return request;
                        })
                        .with(withRole(Grupo.ADMINISTRADOR)) // Autentica como ADMIN.
                        .with(csrf()) // Adiciona o token CSRF.
                )
                .andExpect(status().isBadRequest()) // Espera status 400 Bad Request.
                .andExpect(jsonPath("$", is("Erro ao editar produto: Erro de teste de edição"))); // Verifica a mensagem de erro.

        // Verifica se 'editarProduto' foi chamado uma vez com os argumentos esperados.
        verify(produtoService, times(1)).editarProduto(
                eq(produtoId), eq("Produto Editado"), eq("Desc Editada"), eq(new BigDecimal("150.00")), eq(15), eq(0), any(MultipartFile[].class));
    }

    /**
     * Testa se um usuário ADMINISTRADOR pode habilitar/inabilitar um produto com sucesso.
     *
     * @throws Exception Se ocorrer um erro durante a execução da requisição MockMvc.
     */
    @Test
    @DisplayName("ADMIN: Deve habilitar/inabilitar produto com sucesso")
    void deveHabilitarInabilitarProdutoComSucesso() throws Exception {
        // Arrange
        Long produtoId = 1L; // Define o ID do produto.
        // Configura o mock: quando 'habilitarInabilitar' for chamado, não faça nada (pois é um método void).
        doNothing().when(produtoService).habilitarInabilitar(produtoId);

        // Act & Assert
        mockMvc.perform(patch("/api/produtos/{id}/status", produtoId) // Realiza requisição PATCH para "/status".
                        .contentType(MediaType.APPLICATION_JSON) // Tipo de conteúdo.
                        .with(withRole(Grupo.ADMINISTRADOR)) // Autentica como ADMIN.
                        .with(csrf()) // Adiciona o token CSRF.
                )
                .andExpect(status().isOk()); // Espera status 200 OK.

        // Verifica se 'habilitarInabilitar' foi chamado uma vez com o ID correto.
        verify(produtoService, times(1)).habilitarInabilitar(produtoId);
    }

    /**
     * Testa se um usuário ESTOQUISTA pode alterar a quantidade em estoque de um produto com sucesso.
     *
     * @throws Exception Se ocorrer um erro durante a execução da requisição MockMvc.
     */
    @Test
    @DisplayName("ESTOQUISTA: Deve alterar estoque de produto com sucesso")
    void deveAlterarEstoqueComSucesso() throws Exception {
        // Arrange
        Long produtoId = 1L; // Define o ID do produto.
        int novaQuantidade = 25; // Define a nova quantidade em estoque.
        // Configura o mock: quando 'alterarQuantidadeEstoque' for chamado, não faça nada.
        doNothing().when(produtoService).alterarQuantidadeEstoque(produtoId, novaQuantidade);

        // Act & Assert
        mockMvc.perform(patch("/api/produtos/{id}/estoque", produtoId) // Realiza requisição PATCH para "/estoque".
                        .param("quantidadeEstoque", String.valueOf(novaQuantidade)) // Adiciona o parâmetro da nova quantidade.
                        .contentType(MediaType.APPLICATION_JSON) // Tipo de conteúdo.
                        .with(withRole(Grupo.ESTOQUISTA)) // Autentica como ESTOQUISTA.
                        .with(csrf()) // Adiciona o token CSRF.
                )
                .andExpect(status().isOk()) // Espera status 200 OK.
                .andExpect(jsonPath("$", is("Estoque atualizado com sucesso"))); // Verifica a mensagem de sucesso.

        // Verifica se 'alterarQuantidadeEstoque' foi chamado uma vez com os argumentos corretos.
        verify(produtoService, times(1)).alterarQuantidadeEstoque(produtoId, novaQuantidade);
    }

    /**
     * Testa se a alteração de estoque retorna 400 Bad Request quando o serviço lança uma exceção.
     *
     * @throws Exception Se ocorrer um erro durante a execução da requisição MockMvc.
     */
    @Test
    @DisplayName("ESTOQUISTA: Deve retornar bad request ao alterar estoque com erro no serviço")
    void deveRetornarBadRequestAoAlterarEstoqueComErro() throws Exception {
        // Arrange
        Long produtoId = 1L; // Define o ID do produto.
        int novaQuantidade = 25; // Define a nova quantidade.
        // Configura o mock: quando 'alterarQuantidadeEstoque' for chamado, lance uma RuntimeException.
        doThrow(new RuntimeException("Erro ao buscar produto para estoque")).when(produtoService).alterarQuantidadeEstoque(produtoId, novaQuantidade);

        // Act & Assert
        mockMvc.perform(patch("/api/produtos/{id}/estoque", produtoId) // Realiza requisição PATCH.
                        .param("quantidadeEstoque", String.valueOf(novaQuantidade)) // Parâmetro de quantidade.
                        .contentType(MediaType.APPLICATION_JSON) // Tipo de conteúdo.
                        .with(withRole(Grupo.ESTOQUISTA)) // Autentica como ESTOQUISTA.
                        .with(csrf()) // Adiciona o token CSRF.
                )
                .andExpect(status().isBadRequest()) // Espera status 400 Bad Request.
                .andExpect(jsonPath("$", is("Erro ao atualizar estoque: " + "Erro ao buscar produto para estoque"))); // Verifica a mensagem de erro.

        // Verifica se 'alterarQuantidadeEstoque' foi chamado uma vez com os argumentos corretos.
        verify(produtoService, times(1)).alterarQuantidadeEstoque(produtoId, novaQuantidade);
    }

    // --- Testes de Segurança (Acesso Não Autorizado) ---

    /**
     * Testa se um usuário CLIENTE é proibido (403 Forbidden) de listar produtos da área administrativa.
     *
     * @throws Exception Se ocorrer um erro durante a execução da requisição MockMvc.
     */
    @Test
    @DisplayName("CLIENTE: Deve retornar 403 Forbidden ao tentar listar produtos admin")
    void clienteNaoDeveListarProdutosAdmin() throws Exception {
        // Arrange (Nenhuma configuração específica do serviço é necessária, pois o acesso será barrado antes)
        // Act & Assert
        mockMvc.perform(get("/api/produtos/admin") // Realiza requisição GET para a rota de admin.
                        .param("page", "0") // Parâmetros de página.
                        .param("size", "10") // Parâmetros de tamanho.
                        .contentType(MediaType.APPLICATION_JSON) // Tipo de conteúdo.
                        .with(withRole(Grupo.CLIENTE))) // Autentica como CLIENTE.
                .andExpect(status().isForbidden()); // Espera status 403 Forbidden.
    }

    /**
     * Testa se um usuário CLIENTE é proibido (403 Forbidden) de cadastrar produtos.
     *
     * @throws Exception Se ocorrer um erro durante a execução da requisição MockMvc.
     */
    @Test
    @DisplayName("CLIENTE: Deve retornar 403 Forbidden ao tentar cadastrar produto")
    void clienteNaoDeveCadastrarProduto() throws Exception {
        // Arrange
        MockMultipartFile imagem = new MockMultipartFile("imagens", "imagem.png", "image/png", "some image data".getBytes()); // Cria arquivo mock.

        // Act & Assert
        mockMvc.perform(multipart("/api/produtos") // Realiza requisição multipart POST.
                        .file(imagem) // Anexa o arquivo.
                        .param("nome", "Novo Produto") // Parâmetros da requisição.
                        .param("descricaoDetalhada", "Detalhes do novo produto")
                        .param("preco", "123.45")
                        .param("quantidadeEstoque", "50")
                        .param("imagemPadrao", "0")
                        .with(request -> { // Garante que é um POST.
                            request.setMethod("POST");
                            return request;
                        })
                        .with(withRole(Grupo.CLIENTE)) // Autentica como CLIENTE.
                        .with(csrf()) // Adiciona o token CSRF (importante para que o Spring Security processe a requisição antes de barrar por autorização).
                )
                .andExpect(status().isForbidden()); // Espera status 403 Forbidden.
    }

    /**
     * Testa se um usuário CLIENTE é proibido (403 Forbidden) de editar produtos.
     *
     * @throws Exception Se ocorrer um erro durante a execução da requisição MockMvc.
     */
    @Test
    @DisplayName("CLIENTE: Deve retornar 403 Forbidden ao tentar editar produto")
    void clienteNaoDeveEditarProduto() throws Exception {
        // Arrange
        Long produtoId = 1L; // Define o ID do produto.
        MockMultipartFile imagem = new MockMultipartFile("imagens", "imagem.png", "image/png", "some image data".getBytes()); // Cria arquivo mock.

        // Act & Assert
        mockMvc.perform(multipart("/api/produtos/{id}", produtoId) // Realiza requisição multipart PUT.
                        .file(imagem) // Anexa o arquivo.
                        .param("nome", "Produto Editado") // Parâmetros da requisição.
                        .param("descricaoDetalhada", "Desc Editada")
                        .param("preco", "150.00")
                        .param("quantidadeEstoque", "15")
                        .param("imagemPadrao", "0")
                        .with(request -> { // Garante que é um PUT.
                            request.setMethod("PUT");
                            return request;
                        })
                        .with(withRole(Grupo.CLIENTE)) // Autentica como CLIENTE.
                        .with(csrf()) // Adiciona o token CSRF.
                )
                .andExpect(status().isForbidden()); // Espera status 403 Forbidden.
    }

    /**
     * Testa se um usuário ESTOQUISTA é proibido (403 Forbidden) de habilitar/inabilitar produtos.
     * (Essa funcionalidade deve ser exclusiva do ADMINISTRADOR).
     *
     * @throws Exception Se ocorrer um erro durante a execução da requisição MockMvc.
     */
    @Test
    @DisplayName("ESTOQUISTA: Deve retornar 403 Forbidden ao tentar habilitar/inabilitar produto")
    void estoquistaNaoDeveHabilitarInabilitarProduto() throws Exception {
        // Arrange (Nenhuma configuração específica do serviço é necessária)
        Long produtoId = 1L; // Define o ID do produto.

        // Act & Assert
        mockMvc.perform(patch("/api/produtos/{id}/status", produtoId) // Realiza requisição PATCH para "/status".
                        .contentType(MediaType.APPLICATION_JSON) // Tipo de conteúdo.
                        .with(withRole(Grupo.ESTOQUISTA)) // Autentica como ESTOQUISTA.
                        .with(csrf()) // Adiciona o token CSRF.
                )
                .andExpect(status().isForbidden()); // Espera status 403 Forbidden.
    }

    /**
     * Testa se um usuário ADMINISTRADOR é proibido (403 Forbidden) de alterar o estoque de produtos.
     * (Essa funcionalidade deve ser exclusiva do ESTOQUISTA, conforme definido pela anotação @PreAuthorize).
     *
     * @throws Exception Se ocorrer um erro durante a execução da requisição MockMvc.
     */
    @Test
    @DisplayName("ADMIN: Deve retornar 403 Forbidden ao tentar alterar estoque")
    void adminNaoDeveAlterarEstoque() throws Exception {
        // Arrange
        Long produtoId = 1L; // Define o ID do produto.
        int novaQuantidade = 25; // Define a nova quantidade.

        // Simula um usuário ADMINISTRADOR com APENAS a autoridade ADMINISTRADOR.
        // Isso é para garantir que nenhuma outra autoridade (como ESTOQUISTA) esteja sendo adicionada implicitamente.
        org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor adminUser =
                user("adminUser").password("password").authorities(new SimpleGrantedAuthority(Grupo.ADMINISTRADOR.name()));

        // Act & Assert
        mockMvc.perform(patch("/api/produtos/{id}/estoque", produtoId) // Realiza requisição PATCH para "/estoque".
                        .param("quantidadeEstoque", String.valueOf(novaQuantidade)) // Parâmetro de quantidade.
                        .contentType(MediaType.APPLICATION_JSON) // Tipo de conteúdo.
                        .with(adminUser) // Usa o PostProcessor de usuário ADMINISTRADOR explicitamente.
                        .with(csrf()) // Adiciona o token CSRF.
                )
                .andExpect(status().isForbidden()); // Espera status 403 Forbidden.
    }
}