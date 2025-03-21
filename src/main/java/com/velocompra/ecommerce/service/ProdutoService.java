package com.velocompra.ecommerce.service;

import com.velocompra.ecommerce.model.Produto;
import com.velocompra.ecommerce.repository.ProdutoRepository;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Value("${produto.upload-dir:uploads/}")
    private String uploadDir;

    public Page<Produto> listarTodosProdutosAtivos(Pageable pageable) {
        return produtoRepository.findByAtivoTrue(pageable);
    }

    public Page<Produto> buscarPorNomeTodos(String nome, Pageable pageable) {
        return produtoRepository.findByNomeContainingIgnoreCase(nome, pageable);
    }

    public Page<Produto> listarTodosComInativos(Pageable pageable) {
        return produtoRepository.findAll(pageable); // sem filtro de ativos!
    }

    public Page<Produto> buscarPorNome(String nome, Pageable pageable) {
        return produtoRepository.findByNomeContainingIgnoreCase(nome, pageable);
    }

    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id).orElse(null);
    }

    public Produto cadastrarProduto(String nome, String descricaoDetalhada, BigDecimal preco, int quantidadeEstoque,
                                    int imagemPadrao, MultipartFile[] imagens) throws Exception {

        if (nome.isBlank() || descricaoDetalhada.isBlank() || preco == null || preco.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Dados inválidos para cadastro do produto.");
        }

        Produto produto = new Produto();
        produto.setNome(nome);
        produto.setDescricaoDetalhada(descricaoDetalhada);
        produto.setPreco(preco);
        produto.setQuantidadeEstoque(quantidadeEstoque);
        produto.setAtivo(true);

        // Salva imagens
        Files.createDirectories(Paths.get(uploadDir));
        List<String> imagensSalvas = new ArrayList<>();

        for (MultipartFile imagem : imagens) {
            String nomeArquivo = System.currentTimeMillis() + "_" + imagem.getOriginalFilename();
            Path caminho = Paths.get(uploadDir, nomeArquivo);

            imagem.transferTo(caminho.toFile());
            imagensSalvas.add(nomeArquivo);
        }

        produto.setImagens(imagensSalvas);

        if (imagemPadrao >= 0 && imagemPadrao < imagensSalvas.size()) {
            produto.setImagemPadrao(imagensSalvas.get(imagemPadrao));
        } else {
            throw new IllegalArgumentException("Índice de imagem padrão inválido.");
        }

        return produtoRepository.save(produto);
    }

    /**
     * Valida os campos obrigatórios antes de criar um produto.
     */
    private void validarCamposObrigatorios(String nome, String descricaoDetalhada, BigDecimal preco,
                                           int quantidadeEstoque, MultipartFile[] imagens) throws Exception {

        if (nome == null || nome.trim().isEmpty()) {
            throw new Exception("O nome do produto é obrigatório.");
        }

        if (descricaoDetalhada == null || descricaoDetalhada.trim().isEmpty()) {
            throw new Exception("A descrição detalhada do produto é obrigatória.");
        }

        if (preco == null || preco.compareTo(BigDecimal.ZERO) <= 0) {
            throw new Exception("O preço do produto deve ser maior que zero.");
        }

        if (quantidadeEstoque < 0) {
            throw new Exception("A quantidade em estoque não pode ser negativa.");
        }

        if (imagens == null || imagens.length == 0) {
            throw new Exception("Pelo menos uma imagem deve ser enviada para o produto.");
        }
    }

    public void habilitarInabilitar(Long id) {
        Produto produto = buscarPorId(id);
        if (produto == null) {
            throw new RuntimeException("Produto não encontrado");
        }

        produto.setAtivo(!produto.isAtivo());
        produtoRepository.save(produto);
    }

    public Produto editarProduto(Long id, String nome, String descricaoDetalhada, BigDecimal preco,
                                 int quantidadeEstoque, int imagemPadrao, MultipartFile[] novasImagens) throws Exception {

        Produto produto = buscarPorId(id);

        if (produto == null) {
            throw new RuntimeException("Produto não encontrado!");
        }

        produto.setNome(nome);
        produto.setDescricaoDetalhada(descricaoDetalhada);
        produto.setPreco(preco);
        produto.setQuantidadeEstoque(quantidadeEstoque);

        // Atualizando imagens, se forem enviadas
        if (novasImagens != null && novasImagens.length > 0) {
            // Cria o diretório de uploads (se necessário)
            Files.createDirectories(Paths.get(uploadDir));

            List<String> novasImagensSalvas = new ArrayList<>();

            for (MultipartFile imagem : novasImagens) {
                String nomeArquivo = System.currentTimeMillis() + "_" + imagem.getOriginalFilename();
                Path caminho = Paths.get(uploadDir, nomeArquivo);

                imagem.transferTo(caminho.toFile());
                novasImagensSalvas.add(nomeArquivo);
            }

            produto.setImagens(novasImagensSalvas);

            if (imagemPadrao >= 0 && imagemPadrao < novasImagensSalvas.size()) {
                produto.setImagemPadrao(novasImagensSalvas.get(imagemPadrao));
            } else {
                throw new IllegalArgumentException("Índice de imagem padrão inválido.");
            }

        } else {
            // Apenas troca a imagem padrão entre as existentes
            if (imagemPadrao >= 0 && imagemPadrao < produto.getImagens().size()) {
                produto.setImagemPadrao(produto.getImagens().get(imagemPadrao));
            }
        }

        return produtoRepository.save(produto);
    }

    public void alterarQuantidadeEstoque(Long id, int novaQuantidade) {
        Produto produto = buscarPorId(id);

        if (produto == null) {
            throw new RuntimeException("Produto não  encontrado");
        }

        produto.setQuantidadeEstoque(novaQuantidade);
        produtoRepository.save(produto);
    }

}
