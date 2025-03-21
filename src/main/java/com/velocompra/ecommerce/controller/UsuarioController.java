package com.velocompra.ecommerce.controller;

import com.velocompra.ecommerce.dto.UsuarioDTO;
import com.velocompra.ecommerce.model.Usuario;
import com.velocompra.ecommerce.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "http://localhost:3000")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // LISTAR USUÁRIOS
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

    // CADASTRAR USUÁRIO
    @PostMapping
    public ResponseEntity<?> cadastrarUsuario(@RequestBody UsuarioDTO usuarioDTO) {

            usuarioService.cadastrarUsuario(usuarioDTO);
            return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarUsuario(@PathVariable Long id, @RequestBody UsuarioDTO usuarioDTO) {
        usuarioService.alterarUsuario(id, usuarioDTO);
        return ResponseEntity.ok().build();
    }

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

        return  ResponseEntity.ok(usuarioDTO);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> alterarStatus(@PathVariable Long id) {
        usuarioService.habilitarDesabilitarUsuario(id);
        return ResponseEntity.ok().build();
    }

}
