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


    public List<Usuario> listarTodosUsuarios() {
        return usuarioRepository.findAll();
    }

    public List<Usuario> buscarPorNome(String nome) {
        return usuarioRepository.findByNomeContainingIgnoreCase(nome);
    }

    public Usuario buscarUsuariosPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

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

    public void alterarUsuario(Long id, UsuarioDTO usuarioDTO) {
        Usuario usuario = buscarUsuariosPorId(id);
        if (usuario == null) {
            throw new RuntimeException("Usuário não encontrado");
        }

        // ✅ Atualiza os campos permitidos
        usuario.setNome(usuarioDTO.getNome());
        usuario.setCpf(usuarioDTO.getCpf());
        usuario.setGrupo(usuarioDTO.getGrupo());

        // ✅ Se vier senha no DTO, atualiza com criptografia
        if (usuarioDTO.getSenha() != null && usuarioDTO.getSenha().isEmpty()) {
            usuario.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
        }

        usuarioRepository.save(usuario);
    }

    public void habilitarDesabilitarUsuario(Long id) {
        Usuario usuario = buscarUsuariosPorId(id);
        if (usuario == null) {
            throw new RuntimeException("Usuário não encontrado");
        }

        usuario.setAtivo(!usuario.isAtivo());
        usuarioRepository.save(usuario);
    }

    public void alterarSenhaUsuario(Long usuarioId, String novaSenha) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));


        String senhaCriptografada = passwordEncoder.encode(novaSenha);
        usuario.setSenha(senhaCriptografada);
        usuarioRepository.save(usuario);
        System.out.println("Senha alterada com sucesso para o usuário: " + usuario.getEmail());
    }
}
