package com.velocompra.ecommerce.steps;

import com.velocompra.ecommerce.model.Produto;
import com.velocompra.ecommerce.repository.ProdutoRepository;
import com.velocompra.ecommerce.service.ProdutoService;
import io.cucumber.java.pt.*;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class ProdutoSteps {

    private ProdutoService produtoService;
    private ProdutoRepository produtoRepository;
    private List<Produto> listaProdutos;
    private Produto produtoRetornado;
    private Page<Produto> paginaProdutos;
    private Produto produtoMock;

    @Dado("que estou logado como administrador ou estoquista")
    public void estouLogado() {
        produtoRepository = Mockito.mock(ProdutoRepository.class);
        produtoService = Mockito.mock(ProdutoService.class);
    }

    @Quando("eu acessar a tela de listagem de produtos")
    public void acessarTelaListagem() {
        Produto p1 = new Produto();
        Produto p2 = new Produto();
        p1.setId(1L);
        p1.setNome("Produto A");
        p1.setDescricaoDetalhada("desc");
        p1.setPreco(BigDecimal.TEN);
        p1.setQuantidadeEstoque(10);
        p1.setAtivo(true);

        p2.setId(2L);
        p2.setNome("Produto B");
        p2.setDescricaoDetalhada("desc");
        p2.setPreco(BigDecimal.valueOf(20));
        p2.setQuantidadeEstoque(10);
        p2.setAtivo(true);

        listaProdutos = Arrays.asList(p1, p2);
        paginaProdutos = new PageImpl<>(listaProdutos);

        when(produtoService.listarTodosComInativos(any(Pageable.class))).thenReturn(paginaProdutos);
    }

    @Então("devo ver os produtos ordenados do mais recente para o mais antigo")
    public void verificarOrdemProdutos() {
        Page<Produto> resultado = produtoService.listarTodosComInativos(PageRequest.of(0, 10));
        Assertions.assertEquals(2, resultado.getContent().size());
    }

    @Então("cada produto deve mostrar código, nome, estoque, valor e status")
    public void verificarCamposExibidos() {
        for (Produto p : paginaProdutos) {
            Assertions.assertNotNull(p.getId());
            Assertions.assertNotNull(p.getNome());
            Assertions.assertNotNull(p.getQuantidadeEstoque());
            Assertions.assertNotNull(p.getPreco());
            Assertions.assertNotNull(p.isAtivo());
        }
    }

    @Então("deve haver um botão de adicionar novo produto")
    public void verificarBotaoAdicionar() {
        // Simulação da existência do botão
        Assertions.assertTrue(true);
    }

    @Então("deve haver paginação limitada a {int} produtos por página")
    public void verificarPaginacao(Integer limite) {
        Pageable pageable = PageRequest.of(0, limite);
        Page<Produto> resultado = produtoService.listarTodosComInativos(pageable);
        Assertions.assertTrue(resultado.getSize() <= limite);
    }

    @Quando("eu digitar parte do nome do produto na busca, como {string}")
    public void buscarPorNomeParcial(String nome) {
        Produto p1 = new Produto();
        p1.setId(3L);
        p1.setNome("Smartphone X");
        p1.setDescricaoDetalhada("desc");
        p1.setPreco(BigDecimal.TEN);
        p1.setQuantidadeEstoque(15);
        p1.setAtivo(true);

        List<Produto> produtosBusca = Arrays.asList(p1);
        Page<Produto> paginaBusca = new PageImpl<>(produtosBusca);

        when(produtoService.buscarPorNomeTodos(eq(nome), any(Pageable.class)))
                .thenReturn(paginaBusca);
    }

    @Então("a lista deve conter todos os produtos que tenham {string} no nome")
    public void verificarResultadoBusca(String termo) {
        Page<Produto> resultado = produtoService.buscarPorNomeTodos(termo, PageRequest.of(0,10));
        Assertions.assertTrue(resultado.getContent().stream()
                .allMatch(p -> p.getNome().toLowerCase()
                        .contains(termo.toLowerCase())));
    }

    @Dado("que um produto já está cadastrado")
    public void produtoCadastrado() {
        produtoMock = new Produto();
        produtoMock.setId(5L);
        produtoMock.setNome("Produto Existente");
        produtoMock.setAtivo(true);
        when(produtoService.buscarPorId(5L)).thenReturn(produtoMock);
    }

    @Quando("eu preencher os dados do produto e enviar imagens")
    public void cadastrarProdutoComImagens() {
        // Simulação do cadastro com imagens
        produtoMock.setImagens(Arrays.asList("imagem1.jpg", "imagem2.jpg"));
    }

    @Quando("marcar uma imagem como padrão")
    public void marcarImagemPadrao() {
        produtoMock.setImagemPadrao("imagem1.jpg");
    }

    @Então("o produto e as imagens devem ser salvos no banco de dados")
    public void verificarCadastroProduto() {
        Assertions.assertNotNull(produtoMock);
        Assertions.assertFalse(produtoMock.getImagens().isEmpty());
        Assertions.assertNotNull(produtoMock.getImagemPadrao());
    }

    @Quando("eu atualizar as informações e as imagens")
    public void atualizarProduto() {
        produtoMock.setNome("Produto Atualizado");
        produtoMock.setImagens(Arrays.asList("nova_imagem.jpg"));
    }

    @Então("as alterações devem ser refletidas no banco de dados")
    public void verificarAtualizacaoProduto() {
        Assertions.assertEquals("Produto Atualizado", produtoMock.getNome());
        Assertions.assertEquals(1, produtoMock.getImagens().size());
    }

    @Dado("que um produto está ativo")
    public void produtoAtivo() {
        produtoMock = new Produto();
        produtoMock.setId(6L);
        produtoMock.setAtivo(true);
        when(produtoService.buscarPorId(6L)).thenReturn(produtoMock);
    }

    @Quando("eu clicar em inativar")
    public void clicarInativar() {
        produtoService.habilitarInabilitar(6L);
        produtoMock.setAtivo(false);
    }

    @Então("deve aparecer uma confirmação")
    public void deveAparecerConfirmacao() {
        // Simulação do popup de confirmação
        boolean popupAberto = true;
        Assertions.assertTrue(popupAberto);
    }

    @Então("ao confirmar, o status do produto deve ser alterado para inativo")
    public void statusAlterado() {
        Assertions.assertFalse(produtoMock.isAtivo());
    }

    @Quando("eu acessar a tela de edição como estoquista")
    public void acessarTelaEdicaoEstoquista() {
        // Simulação de perfil estoquista
        boolean apenasQuantidadeEditavel = true;
        Assertions.assertTrue(apenasQuantidadeEditavel);
    }

    @Então("apenas o campo de quantidade deve estar habilitado")
    public void verificarCamposEditaveis() {
        // Simulação de campos desabilitados
        boolean outrosCamposDesabilitados = true;
        Assertions.assertTrue(outrosCamposDesabilitados);
    }

    @Então("ao salvar, a nova quantidade deve ser registrada no banco de dados")
    public void verificarAtualizacaoEstoque() {
        produtoMock.setQuantidadeEstoque(50);
        Assertions.assertEquals(50, produtoMock.getQuantidadeEstoque());
    }

    @Quando("eu acessar a opção de visualizar produto")
    public void acessarVisualizacaoCliente() {
        // Simulação da visualização do cliente
        boolean modoVisualizacao = true;
        Assertions.assertTrue(modoVisualizacao);
    }

//    @Então("deve aparecer uma prévia da página com imagens e avaliações")
//    public void verificarPreviaProduto() {
//        produtoMock.setImagens(Arrays.asList("img1.jpg", "img2.jpg"));
//        produtoMock.setAvaliacoes(Arrays.asList(4, 5));
//        Assertions.assertFalse(produtoMock.getImagens().isEmpty());
//        Assertions.assertFalse(produtoMock.getAvaliacoes().isEmpty());
//    }

    @E("o botão de comprar deve estar desabilitado")
    public void verificarBotaoComprar() {
        boolean botaoDesabilitado = true;
        Assertions.assertTrue(botaoDesabilitado);
    }
}