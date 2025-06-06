package com.velocompra.ecommerce.controller;

import com.velocompra.ecommerce.dto.PageResponseDTO;
import com.velocompra.ecommerce.model.Produto;
import com.velocompra.ecommerce.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

/**
 * Controlador responsável pela gestão de produtos no sistema.
 * Este controlador oferece endpoints públicos para listagem de produtos e privados para operações administrativas, como cadastro, edição e alteração de status.
 */
@RestController
@RequestMapping("/api/produtos")
@CrossOrigin(origins = "http://localhost:3000")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    // 📦 Rotas Públicas (Loja)

    /**
     * Lista produtos ativos ou realiza a busca por nome.
     * Este endpoint permite filtrar os produtos por nome, com paginação.
     *
     * @param nome O nome para filtrar os produtos (opcional).
     * @param pageable Os parâmetros de paginação.
     * @return Uma resposta com a lista de produtos encontrados.
     */
    @GetMapping(produces = "application/json")
    public ResponseEntity<PageResponseDTO<Produto>> listarProdutos(
            @RequestParam(required = false) String nome,
            Pageable pageable
    ) {
        Page<Produto> produtos = (nome != null && !nome.isEmpty())
                ? produtoService.buscarPorNome(nome, pageable)
                : produtoService.listarTodosProdutosAtivos(pageable);

        PageResponseDTO<Produto> responseDTO = new PageResponseDTO<>(produtos);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Busca um produto pelo seu ID.
     *
     * @param id O ID do produto a ser buscado.
     * @return Uma resposta com o produto encontrado ou uma resposta de "não encontrado" se o produto não existir.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable Long id) {
        Produto produto = produtoService.buscarPorId(id);
        return produto != null ? ResponseEntity.ok(produto) : ResponseEntity.notFound().build();
    }

    // 🔒 Rotas Privadas (Admin e Estoquista)

    /**
     * Lista todos os produtos (ativos e inativos) para usuários com permissão de administrador ou estoquista.
     * Este endpoint permite filtrar os produtos por nome e possui paginação.
     *
     * @param nome O nome para filtrar os produtos (opcional).
     * @param pageable Os parâmetros de paginação.
     * @return Uma resposta com a lista de produtos encontrados.
     */
    @GetMapping("/admin")
    @PreAuthorize("hasAnyAuthority('ADMINISTRADOR', 'ESTOQUISTA')")
    public ResponseEntity<Page<Produto>> listarProdutosAdmin(
            @RequestParam(required = false) String nome,
            Pageable pageable
    ) {
        Page<Produto> produtos = (nome != null && !nome.isEmpty())
                ? produtoService.buscarPorNomeTodos(nome, pageable)
                : produtoService.listarTodosComInativos(pageable);

        return ResponseEntity.ok(produtos);
    }

    /**
     * Cadastra um novo produto no sistema. Apenas administradores podem realizar essa operação.
     *
     * @param nome O nome do produto.
     * @param descricaoDetalhada A descrição detalhada do produto.
     * @param preco O preço do produto.
     * @param quantidadeEstoque A quantidade disponível do produto no estoque.
     * @param imagemPadrao O índice da imagem padrão do produto.
     * @param imagens As imagens do produto.
     * @return Uma resposta de sucesso ou erro dependendo do resultado do cadastro.
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<?> cadastrarProduto(
            @RequestParam String nome,
            @RequestParam String descricaoDetalhada,
            @RequestParam BigDecimal preco,
            @RequestParam int quantidadeEstoque,
            @RequestParam int imagemPadrao,
            @RequestParam("imagens") MultipartFile[] imagens
    ) {
        try {
            produtoService.cadastrarProduto(nome, descricaoDetalhada, preco, quantidadeEstoque, imagemPadrao, imagens);
            return ResponseEntity.ok("Produto cadastrado com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao cadastrar produto: " + e.getMessage());
        }
    }

    /**
     * Edita um produto existente no sistema. Apenas administradores podem realizar essa operação.
     *
     * @param id O ID do produto a ser editado.
     * @param nome O novo nome do produto.
     * @param descricaoDetalhada A nova descrição detalhada do produto.
     * @param preco O novo preço do produto.
     * @param quantidadeEstoque A nova quantidade em estoque do produto.
     * @param imagemPadrao O índice da nova imagem padrão do produto.
     * @param imagens As novas imagens do produto.
     * @return Uma resposta com o produto atualizado ou uma resposta de erro.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<?> editarProduto(
            @PathVariable Long id,
            @RequestParam String nome,
            @RequestParam String descricaoDetalhada,
            @RequestParam BigDecimal preco,
            @RequestParam int quantidadeEstoque,
            @RequestParam int imagemPadrao,
            @RequestParam(required = false) MultipartFile[] imagens
    ) {
        try {
            Produto produtoAtualizado = produtoService.editarProduto(id, nome, descricaoDetalhada, preco, quantidadeEstoque, imagemPadrao, imagens);
            return ResponseEntity.ok(produtoAtualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao editar produto: " + e.getMessage());
        }
    }

    /**
     * Altera o status (habilitado ou desabilitado) de um produto. Apenas administradores podem realizar essa operação.
     *
     * @param id O ID do produto cujo status será alterado.
     * @return Uma resposta de sucesso após a alteração do status.
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<?> habilitarInabilitar(@PathVariable Long id) {
        produtoService.habilitarInabilitar(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Atualiza a quantidade em estoque de um produto. Apenas estoquistas podem realizar essa operação.
     *
     * @param id O ID do produto cujo estoque será atualizado.
     * @param quantidadeEstoque A nova quantidade em estoque.
     * @return Uma resposta de sucesso ou erro dependendo do resultado da atualização.
     */
    @PatchMapping("/{id}/estoque")
    @PreAuthorize("hasAuthority('ESTOQUISTA')")
    public ResponseEntity<?> alterarEstoque(@PathVariable Long id, @RequestParam int quantidadeEstoque) {
        try {
            produtoService.alterarQuantidadeEstoque(id, quantidadeEstoque);
            return ResponseEntity.ok("Estoque atualizado com sucesso");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao atualizar estoque: " + e.getMessage());
        }
    }
}
