package com.velocompra.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.velocompra.ecommerce.dto.UsuarioDTO;
import com.velocompra.ecommerce.model.Grupo;
import com.velocompra.ecommerce.model.Usuario;
import com.velocompra.ecommerce.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.security.test.context.support.WithMockUser;

/**
 * Testes para {@link UsuarioController} usando MockMvc.
 * <p>
 * Cobre as operações de listagem, cadastro, atualização, busca por ID e alteração de status.
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(UsuarioController.class)
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Testa a listagem geral de usuários.
     */
    @Test
    @WithMockUser(authorities = "ADMINISTRADOR")
    void deveListarTodosUsuarios() throws Exception {
        Usuario usuario1 = new Usuario();
        usuario1.setId(1L);
        usuario1.setNome("Caio");
        Usuario usuario2 = new Usuario();
        usuario2.setId(2L);
        usuario2.setNome("Henrique");

        List<Usuario> usuarios = Arrays.asList(usuario1, usuario2);
        when(usuarioService.listarTodosUsuarios()).thenReturn(usuarios);

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",hasSize(2)))
                .andExpect(jsonPath("$[0].nome", is("Caio")))
                .andExpect(jsonPath("$[1].nome", is("Henrique")));
    }

    /**
     * Testa a busca de usuários por nome.
     */
    @Test
    @WithMockUser(authorities = "ADMINISTRADOR")
    void deveBuscarUsuariosPorNome() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Caio");

        when(usuarioService.buscarPorNome("Caio")).thenReturn(List.of(usuario));

        mockMvc.perform(get("/api/usuarios?nome=Caio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome", is("Caio")));
    }

    /**
     * Testa o cadastro de um novo usuário.
     */
    @Test
    @WithMockUser(authorities = "ADMINISTRADOR")
    void deveCadastrarUsuario() throws Exception {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setNome("Caio");
        dto.setEmail("novo@gmail.com");
        dto.setCpf("12345678901");
        dto.setSenha("senha123");
        dto.setGrupo(Grupo.ADMINISTRADOR);

        mockMvc.perform(post("/api/usuarios")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                        .andExpect(status().isOk());
    }

    /**
     * Testa atualização de um usuário.
     */
    @Test
    @WithMockUser(authorities = "ADMINISTRADOR")
    void deveAtualizarUsuario() throws Exception {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setNome("Atualizado");
        dto.setCpf("12345678901");
        dto.setGrupo(Grupo.ESTOQUISTA);

        mockMvc.perform(put("/api/usuarios/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                        .andExpect(status().isOk());
    }

    /**
     * Testa a alteração de status do usuário.
     */
    @Test
    @WithMockUser(authorities = "ADMINISTRADOR")
    void deveAlterarStatusUsuarios() throws Exception {
        mockMvc.perform(patch("/api/usuarios/1/status").with(csrf()))
                        .andExpect(status().isOk());
    }

    /**
     * Testa a busca de um usuário por ID.
     */
    @Test
    @WithMockUser(authorities = "ADMINISTRADOR")
    void deveBuscarUsuarioPorId() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Caio");
        usuario.setCpf("12345678901");
        usuario.setGrupo(Grupo.ADMINISTRADOR);

        when(usuarioService.buscarUsuariosPorId(1L)).thenReturn(usuario);

        mockMvc.perform(get("/api/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Caio")))
                .andExpect(jsonPath("$.cpf", is("12345678901")))
                .andExpect(jsonPath("$.grupo", is("ADMINISTRADOR")));
    }

    /**
     * Testa a resposta 404 quando o usuário não existe.
     */
    @Test
    @WithMockUser(authorities = "ADMINISTRADOR")
    void deveRetornar404SeUsuarioNaoExistir() throws Exception {
        when(usuarioService.buscarUsuariosPorId(99L)).thenReturn(null);

        mockMvc.perform(get("/api/usuarios/99"))
                .andExpect(status().isNotFound());
    }













































}
