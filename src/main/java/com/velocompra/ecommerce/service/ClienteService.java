package com.velocompra.ecommerce.service;

import com.velocompra.ecommerce.dto.ClienteCadastroDTO;
import com.velocompra.ecommerce.dto.ClienteEditarDTO;
import com.velocompra.ecommerce.dto.EnderecoDTO;
import com.velocompra.ecommerce.exception.ConflictException;
import com.velocompra.ecommerce.model.*;
import com.velocompra.ecommerce.repository.ClienteRepository;
import com.velocompra.ecommerce.repository.EnderecoFaturamentoRepository;
import com.velocompra.ecommerce.util.ViaCepClient;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço responsável pela gestão de clientes.
 * Contém métodos para cadastro, atualização de dados, adição de endereços e recuperação de clientes.
 */
@Service
@Transactional
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final EnderecoFaturamentoRepository enderecoFaturamentoRepository;
    private final PasswordEncoder passwordEncoder;
    private final ViaCepClient viaCepUtil;

    /**
     * Construtor com injeção de dependências.
     *
     * @param clienteRepository Repositório de Cliente.
     * @param enderecoFaturamentoRepository Repositório de Endereço de Faturamento.
     * @param passwordEncoder Codificador de senhas.
     * @param viaCepUtil Cliente HTTP para consumir API do ViaCep.
     */
    @Autowired
    public ClienteService(ClienteRepository clienteRepository, EnderecoFaturamentoRepository enderecoFaturamentoRepository,
                          PasswordEncoder passwordEncoder, ViaCepClient viaCepUtil) {
        this.clienteRepository = clienteRepository;
        this.enderecoFaturamentoRepository = enderecoFaturamentoRepository;
        this.passwordEncoder = passwordEncoder;
        this.viaCepUtil = viaCepUtil;
    }

    /**
     * Realiza o cadastro de um novo cliente.
     * Verifica se o CPF e o e-mail já estão cadastrados e realiza a validação de dados.
     * Cria o cliente e associa os endereços de faturamento e entrega.
     *
     * @param dto O DTO contendo os dados do cliente a ser cadastrado.
     * @throws ConflictException Se o CPF ou e-mail já estiverem cadastrados no sistema.
     */
    public Cliente cadastrar(ClienteCadastroDTO dto) {
        // Verifica se já existe um cliente cadastrado com o mesmo CPF
        if (clienteRepository.existsByCpf(dto.getCpf())) {
            // Lança exceção de conflito (HTTP 409) se CPF já estiver em uso
            throw new ConflictException("CPF já cadastrado!");
        }

        // Verifica se já existe um cliente cadastrado com o mesmo e-mail
        if (clienteRepository.existsByEmail(dto.getEmail())) {
            // Lança exceção de conflito (HTTP 409) se e-mail já estiver em uso
            throw new ConflictException("Email já cadastrado!");
        }

        // Cria um novo objeto Cliente e preenche os dados básicos vindos do DTO
        Cliente cliente = new Cliente();
        cliente.setNomeCompleto(dto.getNome()); // nome completo
        cliente.setEmail(dto.getEmail()); // e-mail
        cliente.setCpf(dto.getCpf()); // cpf
        cliente.setDataNascimento(dto.getDataNascimento()); // data de nascimento
        cliente.setGenero(dto.getGenero()); // genero
        cliente.setSenha(passwordEncoder.encode(dto.getSenha())); // criptogrfa a senha antes de salvar

        // Salva o cliente no banco de dados.
        // Se estiver usando @OneToMany ou @OneToOne com CascadeType.ALL, os endereços serão salvos automaticamente depois.
        final Cliente clienteSalvo = clienteRepository.save(cliente);

        //Criação do Endereço de Faturamento
        EnderecoDTO endDTO = dto.getEnderecoFaturamento(); // Obtém o DTO do endereço de faturamento
        EnderecoEntrega viaCep = viaCepUtil.buscarCep(endDTO.getCep()); // Usa o utilitário ViaCep para buscar os dados do endereço com base no CEP


        EnderecoFaturamento faturamento = criarEnderecoFaturamento(viaCep, endDTO, clienteSalvo);
        clienteSalvo.setEnderecoFaturamento(faturamento);

        //Criação da Lista de Endereços de Entrega
        // Mapeia cada endereço de entrega do DTO para um objeto EnderecoEntrega
        List<EnderecoEntrega> enderecosEntrega = dto.getEnderecosEntrega().stream().map(endDTOEntrega -> {
            EnderecoEntrega enderecoEntrega = viaCepUtil.buscarCep(endDTOEntrega.getCep()); // Busca dados do CEP usando o ViaCep
            enderecoEntrega.setNumero(endDTOEntrega.getNumero()); // Preenche dados adicionais do endereço (número e complemento)
            enderecoEntrega.setComplemento(endDTOEntrega.getComplemento());
            // A associação com o cliente será feita ao chamar setEnderecosEntrega (via cascade)
            return enderecoEntrega;
        }).collect(Collectors.toList());

        // Associa a lista de endereços de entrega ao cliente
        clienteSalvo.setEnderecosEntrega(enderecosEntrega);

        // Retorna o cliente com todos os dados cadastrados (inclusive endereços, se cascade estiver correto)
        return clienteSalvo;
    }

    /**
     * Atualiza os dados de um cliente existente.
     * Atualiza nome, data de nascimento, gênero, e senha (se fornecida).
     *
     * @param email O e-mail do cliente a ser atualizado.
     * @param dto O DTO contendo os dados a serem atualizados.
     * @throws RuntimeException Se o cliente não for encontrado ou a senha atual estiver incorreta.
     */
    @Transactional
    public void atualizarDados(String email, ClienteEditarDTO dto) {
        Cliente cliente = clienteRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        cliente.setNomeCompleto(dto.getNome());
        cliente.setDataNascimento(dto.getDataNascimento());
        cliente.setGenero(dto.getGenero());

        EnderecoDTO endDTO = dto.getEnderecoFaturamento();
        if (endDTO != null) {
            EnderecoEntrega viaCep = viaCepUtil.buscarCep(endDTO.getCep());
            EnderecoFaturamento faturamento = criarEnderecoFaturamento(viaCep, endDTO, cliente);
            enderecoFaturamentoRepository.save(faturamento);
            cliente.setEnderecoFaturamento(faturamento);
        }

        if (dto.getSenhaAtual() != null && !dto.getSenhaAtual().isEmpty()) {
            if (!passwordEncoder.matches(dto.getSenhaAtual(), cliente.getSenha())) {
                throw new RuntimeException("Senha atual incorreta");
            }
            if (dto.getNovaSenha() == null || dto.getNovaSenha().isEmpty()) {
                throw new RuntimeException("Nova senha não pode ser vazia");
            }
            cliente.setSenha(passwordEncoder.encode(dto.getNovaSenha()));
        }

        clienteRepository.save(cliente);
    }

    /**
     * Adiciona um novo endereço de entrega ao cliente.
     * Caso o endereço seja marcado como "padrão", todos os outros endereços de entrega serão atualizados para não padrão.
     *
     * @param email O e-mail do cliente ao qual o novo endereço será adicionado.
     * @param dto O DTO contendo os dados do novo endereço de entrega.
     * @throws RuntimeException Se o cliente não for encontrado.
     */
    @Transactional
    public void adicionarEnderecoEntrega(String email, EnderecoDTO dto) {
        // Busca o cliente pelo email
        Cliente cliente = clienteRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        // Se o novo endereço for marcado como "padrão", marca os endereços antigos como não padrão
        if (dto.isPadrao()) {
            cliente.getEnderecosEntrega().forEach(e -> e.setPadrao(false));
        }

        EnderecoEntrega novoEndereco = criarEnderecoEntrega(dto, cliente);
        cliente.getEnderecosEntrega().add(novoEndereco);

        // Salva o cliente (isso salvará automaticamente o novo endereço, devido ao relacionamento)
        clienteRepository.save(cliente);
    }

    /**
     * Recupera um cliente pelo seu e-mail.
     *
     * @param email O e-mail do cliente a ser recuperado.
     * @return O cliente encontrado ou null se não encontrado.
     */
    public Cliente getClienteByEmail(String email) {
        return clienteRepository.findByEmail(email).orElse(null);
    }

    /**
     * Método auxiliar para construir EnderecoFaturamento com base no ViaCep e no DTO.
     */

    private static EnderecoFaturamento criarEnderecoFaturamento(EnderecoEntrega viaCep,
                   EnderecoDTO enderecoDTO, Cliente cliente) {
        EnderecoFaturamento faturamento = new EnderecoFaturamento();
        faturamento.setCep(viaCep.getCep());
        faturamento.setLogradouro(viaCep.getLogradouro());
        faturamento.setBairro(viaCep.getBairro());
        faturamento.setCidade(viaCep.getCidade());
        faturamento.setUf(viaCep.getUf());
        faturamento.setNumero(enderecoDTO.getNumero());
        faturamento.setComplemento(enderecoDTO.getComplemento());
        faturamento.setCliente(cliente);
        return faturamento;

    }

    /**
     * Método auxiliar para construir um novo EnderecoEntrega a partir do DTO.
     */
    private static EnderecoEntrega criarEnderecoEntrega(EnderecoDTO dto, Cliente cliente) {
        EnderecoEntrega novoEndereco = new EnderecoEntrega();
        novoEndereco.setCep(dto.getCep());
        novoEndereco.setLogradouro(dto.getLogradouro());
        novoEndereco.setNumero(dto.getNumero());
        novoEndereco.setComplemento(dto.getComplemento());
        novoEndereco.setBairro(dto.getBairro());
        novoEndereco.setCidade(dto.getCidade());
        novoEndereco.setUf(dto.getUf());
        novoEndereco.setPadrao(dto.isPadrao());
        novoEndereco.setCliente(cliente);
        return novoEndereco;
    }
}
