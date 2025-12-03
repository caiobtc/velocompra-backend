package com.velocompra.ecommerce.service;

import com.velocompra.ecommerce.model.Produto;
import com.velocompra.ecommerce.repository.ProdutoRepository;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Serviço responsável pela gestão dos produtos.
 * Contém métodos para cadastro, edição, listagem, habilitação/desabilitação e atualização de estoque de produtos.
 */
@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Value("${produto.upload-dir:uploads/}")
    private  String uploadDir;

    /**
     * Lista todos os produtos ativos, paginados.
     *
     * @param pageable O parâmetro de paginação.
     * @return Uma página contendo produtos ativos.
     */
    public Page<Produto> listarTodosProdutosAtivos(Pageable pageable) {
        return produtoRepository.findByAtivoTrue(pageable);
    }

    /**
     * Busca produtos pelo nome, de forma insensível a maiúsculas e minúsculas, com paginação.
     *
     * @param nome O nome do produto a ser buscado.
     * @param pageable O parâmetro de paginação.
     * @return Uma página de produtos encontrados.
     */
    public Page<Produto> buscarPorNomeTodos(String nome, Pageable pageable) {
        return produtoRepository.findByNomeContainingIgnoreCase(nome, pageable);
    }

    /**
     * Lista todos os produtos, incluindo os inativos, paginados.
     *
     * @param pageable O parâmetro de paginação.
     * @return Uma página de todos os produtos.
     */
    public Page<Produto> listarTodosComInativos(Pageable pageable) {
        return produtoRepository.findAll(pageable); // sem filtro de ativos!
    }

    /**
     * Busca produtos pelo nome, de forma insensível a maiúsculas e minúsculas, com paginação.
     *
     * @param nome O nome do produto a ser buscado.
     * @param pageable O parâmetro de paginação.
     * @return Uma página de produtos encontrados.
     */
    public Page<Produto> buscarPorNome(String nome, Pageable pageable) {
        return produtoRepository.findByNomeContainingIgnoreCase(nome, pageable);
    }

    /**
     * Busca um produto pelo ID.
     *
     * @param id O ID do produto a ser buscado.
     * @return O produto encontrado, ou null se não encontrado.
     */
    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id).orElse(null);
    }

    /**
     * Cadastra um novo produto.
     * Valida os dados obrigatórios, cria diretórios para upload de imagens e salva as imagens do produto.
     *
     * @param nome O nome do produto.
     * @param descricaoDetalhada A descrição detalhada do produto.
     * @param preco O preço do produto.
     * @param quantidadeEstoque A quantidade de estoque do produto.
     * @param imagemPadrao O índice da imagem padrão.
     * @param imagens As imagens do produto.
     * @return O produto recém-criado.
     * @throws Exception Se os dados forem inválidos ou houver um erro ao salvar as imagens.
     */
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
     *
     * @param nome O nome do produto.
     * @param descricaoDetalhada A descrição detalhada do produto.
     * @param preco O preço do produto.
     * @param quantidadeEstoque A quantidade de estoque do produto.
     * @param imagens As imagens do produto.
     * @throws Exception Se algum campo obrigatório estiver inválido.
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

    /**
     * Habilita ou desabilita um produto, alterando seu status de ativo.
     *
     * @param id O ID do produto a ser habilitado ou desabilitado.
     * @throws RuntimeException Se o produto não for encontrado.
     */
    public void habilitarInabilitar(Long id) {
        Produto produto = buscarPorId(id);
        if (produto == null) {
            throw new RuntimeException("Produto não encontrado");
        }

        produto.setAtivo(!produto.isAtivo());
        produtoRepository.save(produto);
    }

    /**
     * Edita os dados de um produto existente.
     * Permite atualizar o nome, a descrição, o preço, a quantidade em estoque e as imagens do produto.
     *
     * @param id O ID do produto a ser editado.
     * @param nome O novo nome do produto.
     * @param descricaoDetalhada A nova descrição detalhada do produto.
     * @param preco O novo preço do produto.
     * @param quantidadeEstoque A nova quantidade de estoque do produto.
     * @param imagemPadrao O índice da nova imagem padrão.
     * @param novasImagens As novas imagens do produto.
     * @return O produto editado.
     * @throws Exception Se houver algum erro na atualização dos dados.
     */
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

    /**
     * Altera a quantidade em estoque de um produto.
     *
     * @param id O ID do produto cujo estoque será alterado.
     * @param novaQuantidade A nova quantidade em estoque.
     * @throws RuntimeException Se o produto não for encontrado.
     */
    public void alterarQuantidadeEstoque(Long id, int novaQuantidade) {
        Produto produto = buscarPorId(id);

        if (produto == null) {
            throw new RuntimeException("Produto não  encontrado");
        }

        produto.setQuantidadeEstoque(novaQuantidade);
        produtoRepository.save(produto);
    }
}
