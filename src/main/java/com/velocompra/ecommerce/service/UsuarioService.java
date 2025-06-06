package com.velocompra.ecommerce.service;

import com.velocompra.ecommerce.dto.UsuarioDTO;
import com.velocompra.ecommerce.model.Grupo;
import com.velocompra.ecommerce.model.Usuario;
import com.velocompra.ecommerce.repository.UsuarioRepository;
import com.velocompra.ecommerce.validacao.CpfValidador;
import com.velocompra.ecommerce.validacao.EmailValidador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Serviço responsável pela gestão dos usuários.
 * Contém métodos para listar, buscar, cadastrar, alterar, habilitar/desabilitar e alterar a senha de usuários.
 */
@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CpfValidador cpfValidador;

    @Autowired
    private EmailValidador emailValidador;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * Construtor para inicialização do serviço, utilizado para testes unitários.
     *
     * @param usuarioRepository O repositório de usuários.
     * @param cpfValidador O validador de CPF.
     * @param emailValidador O validador de e-mail.
     * @param passwordEncoder O codificador de senhas.
     */
    public UsuarioService(UsuarioRepository usuarioRepository,
                          CpfValidador cpfValidador,
                          EmailValidador emailValidador,
                          BCryptPasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.cpfValidador = cpfValidador;
        this.emailValidador = emailValidador;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Lista todos os usuários cadastrados.
     *
     * @return Uma lista contendo todos os usuários.
     */
    public List<Usuario> listarTodosUsuarios() {
        return usuarioRepository.findAll();
    }

    /**
     * Busca usuários pelo nome. A pesquisa é feita de forma insensível a maiúsculas e minúsculas.
     *
     * @param nome O nome do usuário a ser pesquisado.
     * @return Uma lista de usuários cujo nome contém o valor fornecido.
     */
    public List<Usuario> buscarPorNome(String nome) {
        return usuarioRepository.findByNomeContainingIgnoreCase(nome);
    }

    /**
     * Busca um usuário pelo seu ID.
     *
     * @param id O ID do usuário.
     * @return O usuário encontrado ou null se não encontrado.
     */
    public Usuario buscarUsuariosPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    /**
     * Cadastra um novo usuário. Valida o CPF e o e-mail antes de criar o usuário.
     * Verifica também se o e-mail já está cadastrado.
     *
     * @param usuarioDTO O DTO contendo as informações do usuário a ser cadastrado.
     * @throws RuntimeException Se o CPF ou o e-mail forem inválidos ou se o e-mail já estiver cadastrado.
     */
    public void cadastrarUsuario(UsuarioDTO usuarioDTO) {
        if (!cpfValidador.isValid(usuarioDTO.getCpf())) {
            throw new RuntimeException("CPF inválido");
        }
        if (!emailValidador.isValid(usuarioDTO.getEmail())) {
            throw new RuntimeException("Email inválido");
        }

        boolean existeEmail = usuarioRepository.findByEmail(usuarioDTO.getEmail()).isPresent();
        if (existeEmail) {
            throw new RuntimeException("Email já cadastrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(usuarioDTO.getNome());
        usuario.setCpf(usuarioDTO.getCpf());
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
        usuario.setGrupo(usuarioDTO.getGrupo());
        usuario.setAtivo(true);

        usuarioRepository.save(usuario);
    }

    /**
     * Altera os dados de um usuário existente.
     *
     * @param id O ID do usuário a ser alterado.
     * @param usuarioDTO O DTO contendo os novos dados do usuário.
     * @throws RuntimeException Se o usuário não for encontrado.
     */
    public void alterarUsuario(Long id, UsuarioDTO usuarioDTO) {
        Usuario usuario = buscarUsuariosPorId(id);
        if (usuario == null) {
            throw new RuntimeException("Usuário não encontrado");
        }

        // Atualiza os campos permitidos
        usuario.setNome(usuarioDTO.getNome());
        usuario.setCpf(usuarioDTO.getCpf());
        usuario.setGrupo(usuarioDTO.getGrupo());

        // Atualiza a senha se fornecida no DTO
        if (usuarioDTO.getSenha() != null && usuarioDTO.getSenha().isEmpty()) {
            usuario.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
        }

        usuarioRepository.save(usuario);
    }

    /**
     * Habilita ou desabilita um usuário, alterando o status de sua ativação.
     *
     * @param id O ID do usuário a ser habilitado ou desabilitado.
     * @throws RuntimeException Se o usuário não for encontrado.
     */
    public void habilitarDesabilitarUsuario(Long id) {
        Usuario usuario = buscarUsuariosPorId(id);
        if (usuario == null) {
            throw new RuntimeException("Usuário não encontrado");
        }

        usuario.setAtivo(!usuario.isAtivo());
        usuarioRepository.save(usuario);
    }

    /**
     * Altera a senha de um usuário.
     * A nova senha é criptografada antes de ser salva no banco de dados.
     *
     * @param usuarioId O ID do usuário cuja senha será alterada.
     * @param novaSenha A nova senha a ser definida para o usuário.
     * @throws RuntimeException Se o usuário não for encontrado.
     */
    public void alterarSenhaUsuario(Long usuarioId, String novaSenha) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        String senhaCriptografada = passwordEncoder.encode(novaSenha);
        usuario.setSenha(senhaCriptografada);
        usuarioRepository.save(usuario);
        System.out.println("Senha alterada com sucesso para o usuário: " + usuario.getEmail());
    }
}
