package com.velocompra.ecommerce.service;

import com.velocompra.ecommerce.model.Produto;
import com.velocompra.ecommerce.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Classe de teste unitário da {@link ProdutoService}.
 * Testes para operações CRUD de produtos, incluindo upload de imagens.
 * Tecnologias utilizadas:
 *                      JUnit 5 - Framework de testes
 *                      Mockito - Para simulação de dependências
 *                      MockMultipartFile - Para simular upload de arquivos
 */
@ExtendWith(MockitoExtension.class)
public class ProdutoServiceTest {

    // Constantes para dados de teste
    private static final Long ID_PRODUTO = 1L;
    private static final String NOME_PRODUTO = "Produto Teste";
    private static final String DESCRICAO = "Descrição detalhada";
    private static final BigDecimal PRECO = new BigDecimal("99.99");
    private static final int QUANTIDADE_ESTOQUE = 10;
    private static final String IMAGEM_PADRAO = "imagem.jpg";
    private static final String UPLOAD_DIR = "C:/temp/uploads";

    // Mock do repositório
    @Mock
    private ProdutoRepository produtoRepository;

    // Serviço sob teste
    private ProdutoService produtoService;

    /**
     * Configura o ambiente de teste antes de cada método.
     * Inicializa o serviço com o diretório de upload temporário.
     */
    @BeforeEach
    public void setUp() {
        produtoService = new ProdutoService(produtoRepository, UPLOAD_DIR);
    }

    /**
     * Teste unitário que simula o cadastro de um produto com uma imagem válida.
     * O produto é salvo no repositório
     * O ID do produto retornado está correto
     * O método save foi chamado exatamente uma vez
     */
    @Test
    @DisplayName("Deve cadastrar um produto corretamente com imagem válida")
    public void testCadastrarProduto_ComSucesso() throws Exception {
        // ARRANGE - Prepara os dados de teste
        MockMultipartFile imagem = new MockMultipartFile(
                "imagem",
                "foto.png",
                "image/png",
                "fake-content".getBytes()
        );

        Produto produtoMock = new Produto();
        produtoMock.setId(ID_PRODUTO);

        // Configura o comportamento do mock
        when(produtoRepository.save(any(Produto.class))).thenReturn(produtoMock);

        // ACT - Executa o método a ser testado
        Produto resultado = produtoService.cadastrarProduto(
                NOME_PRODUTO,
                DESCRICAO,
                PRECO,
                QUANTIDADE_ESTOQUE,
                0,
                new MockMultipartFile[]{imagem}
        );

        // ASSERT - Verifica os resultados
        assertNotNull(resultado, "O produto retornado não deve ser nulo");
        assertEquals(ID_PRODUTO, resultado.getId(), "O ID do produto deve corresponder ao mock");

        verify(produtoRepository, times(1)).save(any(Produto.class));
    }

    /**
     * Testa a validação de nome vazio ao cadastrar produto.
     * Verifica se:
     *             Uma exceção IllegalArgumentException é lançada
     *             A mensagem de erro está correta
     *             O repositório não foi chamado
     */
    @Test
    @DisplayName("Deve lançar exceção ao cadastrar produto com nome vazio")
    public void testCadastrarProduto_NomeVazio() {
        // ACT & ASSERT - Verifica a exceção
        Exception excecao = assertThrows(IllegalArgumentException.class,
                () -> {produtoService.cadastrarProduto(
                    "", // nome vazio
                    DESCRICAO,
                    PRECO,
                    QUANTIDADE_ESTOQUE,
                    0,
                    new MockMultipartFile[]{}
            );
        });

        assertEquals("Dados inválidos para cadastro do produto.", excecao.getMessage());
        verify(produtoRepository, never()).save(any());
    }

    /**
     * Testa a alternância do status ativo/inativo de um produto.
     * Verifica se:
     *             O status é alterado corretamente
     *             O produto é salvo no repositório
     */
    @Test
    @DisplayName("Deve alternar status ativo/inativo de produto")
    public void testHabilitarInabilitarProduto() {
        // ARRANGE - Cria produto mockado
        Produto produto = new Produto();
        produto.setId(ID_PRODUTO);
        produto.setAtivo(true);

        // Configura o comportamento do mock
        when(produtoRepository.findById(produto.getId())).thenReturn(Optional.of(produto));
        when(produtoRepository.save(produto)).thenReturn(produto);

        // ACT - Executa o método
        produtoService.habilitarInabilitar(produto.getId());

        // ASSERT - Verifica resultados
        assertFalse(produto.isAtivo(), "O produto deve ser desativado");
        verify(produtoRepository).save(produto);
    }

    /**
     * Testa a alteração de estoque de um produto.
     * Verifica se:
     *             A quantidade em estoque é atualizada
     *             O produto é salvo no repositório
     */
    @Test
    @DisplayName("Deve alterar a quantidade em estoque corretamente")
    public void testAlterarEstoque() {
        // ARRANGE - Configura produto com estoque inicial
        Produto produto = new Produto();
        produto.setId(ID_PRODUTO);
        produto.setQuantidadeEstoque(5);

        // Configura o mock
        when(produtoRepository.findById(produto.getId())).thenReturn(Optional.of(produto));

        // ACT - Altera o estoque
        produtoService.alterarQuantidadeEstoque(produto.getId(), 20);

        // ASSERT - Verifica a nova quantidade
        assertEquals(20, produto.getQuantidadeEstoque(), "O estoque deve ser atualizado");
        verify(produtoRepository).save(produto);
    }

    /**
     * Testa a tentativa de alterar estoque de produto inexistente.
     * Verifica se:
     *             Uma exceção é lançada
     *             A mensagem de erro está correta
     */
    @Test
    @DisplayName("Deve lançar exceção se produto não for encontrado ao alterar estoque")
    public void testAlterarEstoque_ProdutoNaoEncontrado() {
        // Configura o mock para retornar vazio
        when(produtoRepository.findById(ID_PRODUTO)).thenReturn(Optional.empty());

        // ACT & ASSERT - Verifica a exceção
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            produtoService.alterarQuantidadeEstoque(ID_PRODUTO, 5);
        });

        assertEquals("Produto não  encontrado", ex.getMessage());
        verify(produtoRepository, never()).save(any());
    }

    /**
     * Testa a edição de um produto sem alterar as imagens.
     * Verifica se:
     *             Os dados são atualizados corretamente
     *             As imagens originais são mantidas
     *             O produto é salvo no repositório
     */
    @Test
    @DisplayName("Deve editar os dados de um produto corretamente")
    public void testEditarProduto_SemNovasImagens() throws Exception {
        // ARRANGE - Cria produto com imagens
        Produto produto = new Produto();
        produto.setId(ID_PRODUTO);

        // Configura os mocks
        when(produtoRepository.findById(produto.getId())).thenReturn(Optional.of(produto));
        when(produtoRepository.save(produto)).thenReturn(produto);

        // ACT - Executa a edição
        Produto resultado = produtoService.editarProduto(
                ID_PRODUTO,
                NOME_PRODUTO,
                DESCRICAO,
                PRECO,
                QUANTIDADE_ESTOQUE,
                0,
                null // sem novas imagens
        );

        // ASSERT - Verifica as alterações
        assertEquals("Novo Nome", resultado.getNome(), "O nome deve ser atualizado");
        assertEquals("Nova descrição", resultado.getDescricaoDetalhada(), "A descrição deve ser atualizada");
        assertNotNull(resultado.getImagens(), "As imagens devem ser mantidas");
        verify(produtoRepository).save(produto);
    }

    /**
     * Testa a edição de um produto com novas imagens.
     *Verifica se:
     *          Os dados são atualizados corretamente
     *          O produto é salvo no repositório
     */
    @Test
    @DisplayName("Deve editar produto com novas imagens")
    public void testEditarProduto_ComNovasImagens() throws Exception {
        // ARRANGE - Prepara dados de teste
        Produto produto = new Produto();
        produto.setId(ID_PRODUTO);
        produto.setImagens(List.of("imagem-antiga.jpg"));

        MockMultipartFile novaImagem = new MockMultipartFile(
                "imagem",
                "nova-imagem.png",
                "image/png",
                "conteudo".getBytes()
        );

        // Configura os mocks
        when(produtoRepository.findById(produto.getId())).thenReturn(Optional.of(produto));
        when(produtoRepository.save(produto)).thenReturn(produto);

        // ACT - Executa a edição com nova imagem
        Produto resultado = produtoService.editarProduto(
                ID_PRODUTO,
                "Produto Atualizado",
                "Descrição atualizada",
                PRECO,
                QUANTIDADE_ESTOQUE,
                0,
                new MockMultipartFile[]{novaImagem}
        );

        // ASSERT - Verifica resultados
        assertEquals("Produto Atualizado", resultado.getNome());
        verify(produtoRepository).save(produto);
    }
}
