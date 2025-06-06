package com.velocompra.ecommerce.controller;

import com.velocompra.ecommerce.dto.UsuarioDTO;
import com.velocompra.ecommerce.model.Usuario;
import com.velocompra.ecommerce.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador responsável pelas operações de gerenciamento de usuários.
 * Oferece endpoints para listar, cadastrar, atualizar, buscar e alterar o status de usuários.
 */
@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "http://localhost:3000")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Lista todos os usuários ou busca usuários pelo nome.
     * Se o parâmetro 'nome' for fornecido, retorna os usuários que contêm esse nome.
     * Caso contrário, retorna todos os usuários cadastrados.
     *
     * @param nome O nome para busca dos usuários (opcional).
     * @return Uma resposta com a lista de usuários encontrados.
     */
    @GetMapping
    public ResponseEntity<?> listarUsuarios(@RequestParam(required = false) String nome) {
        List<Usuario> usuarios;

        if (nome != null && !nome.isEmpty()) {
            usuarios = usuarioService.buscarPorNome(nome);
        } else {
            usuarios = usuarioService.listarTodosUsuarios();
        }

        return ResponseEntity.ok(usuarios);
    }

    /**
     * Cadastra um novo usuário no sistema com base nos dados fornecidos no DTO.
     *
     * @param usuarioDTO O DTO contendo os dados do novo usuário a ser cadastrado.
     * @return Uma resposta de sucesso após o cadastro do usuário.
     */
    @PostMapping
    public ResponseEntity<?> cadastrarUsuario(@RequestBody UsuarioDTO usuarioDTO) {
        usuarioService.cadastrarUsuario(usuarioDTO);
        return ResponseEntity.ok().build();
    }

    /**
     * Atualiza os dados de um usuário existente.
     *
     * @param id O ID do usuário a ser atualizado.
     * @param usuarioDTO O DTO contendo os dados atualizados do usuário.
     * @return Uma resposta de sucesso após a atualização do usuário.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarUsuario(@PathVariable Long id, @RequestBody UsuarioDTO usuarioDTO) {
        usuarioService.alterarUsuario(id, usuarioDTO);
        return ResponseEntity.ok().build();
    }

    /**
     * Busca um usuário pelo ID.
     *
     * @param id O ID do usuário a ser buscado.
     * @return Uma resposta com os dados do usuário encontrado ou uma resposta de "not found" se o usuário não existir.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> buscarUsuarioPorId(@PathVariable Long id) {
        Usuario usuario = usuarioService.buscarUsuariosPorId(id);

        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }

        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setNome(usuario.getNome());
        usuarioDTO.setCpf(usuario.getCpf());
        usuarioDTO.setGrupo(usuario.getGrupo());

        return ResponseEntity.ok(usuarioDTO);
    }

    /**
     * Altera o status (ativo/desabilitado) de um usuário.
     *
     * @param id O ID do usuário cujo status será alterado.
     * @return Uma resposta de sucesso após a alteração do status do usuário.
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> alterarStatus(@PathVariable Long id) {
        usuarioService.habilitarDesabilitarUsuario(id);
        return ResponseEntity.ok().build();
    }

}
