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

@RestController
@RequestMapping("/api/produtos")
@CrossOrigin(origins = "http://localhost:3000")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    // 📦 Rotas Públicas (Loja)
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

    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable Long id) {
        Produto produto = produtoService.buscarPorId(id);
        return produto != null ? ResponseEntity.ok(produto) : ResponseEntity.notFound().build();
    }

    // 🔒 Rotas Privadas (Admin e Estoquista)
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

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<?> habilitarInabilitar(@PathVariable Long id) {
        produtoService.habilitarInabilitar(id);
        return ResponseEntity.ok().build();
    }

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
