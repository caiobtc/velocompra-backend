package com.velocompra.ecommerce.service;

import com.velocompra.ecommerce.dto.UsuarioDTO;
import com.velocompra.ecommerce.model.Grupo;
import com.velocompra.ecommerce.model.Usuario;
import com.velocompra.ecommerce.repository.UsuarioRepository;
import com.velocompra.ecommerce.validacao.CpfValidador;
import com.velocompra.ecommerce.validacao.EmailValidador;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

/**
 * Classe de teste para {@link UsuarioService}.
 * Testes unitários para os métodos de cadastro, listagem, atualização e alteração de status de usuários.
 * Tecnologias utilizadas:
 * JUnit 5 - Framework de testes
 * Mockito - Para simulação de dependências
 * Hamcrest - Para asserções mais expressivas
 */
@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    // Dados constantes para os testes
    private static final Long ID_USUARIO = 1L;
    private static final String NOME_VALIDO = "Caio Vieira";
    private static final String CPF_VALIDO = "04601759200";
    private static final String EMAIL_VALIDO = "teste@valido.com";
    private static final String SENHA = "senha123";
    private static final String SENHA_CRIPTOFRAFADA = "senhaCriptografada123";

    // Mocks das dependencias
    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private CpfValidador cpfValidador;

    @Mock
    private EmailValidador emailValidador;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    // Classe sob teste (injetada automaticamente com os mocks)
    @InjectMocks
    private UsuarioService usuarioService;


    /**
     * Testa o cadastro bem-sucedido de um usuario com dados válidos.
     *
     * Verifica se:
     * - Os validadores de CPF e email são chamados.
     * - A senha é criptografada corretamente.
     * - A senha é criptografada corretamente.
     * - O usuário é salvo no repositório.
     */
    @Test
    @DisplayName("Deve cadastrar usuario com os dados válidos")
    void deveCadastrarUsuarioComDadosValidos() {

        UsuarioDTO dto = new UsuarioDTO();
        dto.setNome(NOME_VALIDO);
        dto.setCpf(CPF_VALIDO);
        dto.setEmail(EMAIL_VALIDO);
        dto.setSenha(SENHA);
        dto.setGrupo(Grupo.ADMINISTRADOR);

        // Configura os mocks para retornar respostas simuladas
        when(cpfValidador.isValid(dto.getCpf())).thenReturn(true);
        when(emailValidador.isValid(dto.getEmail())).thenReturn(true);
        when(usuarioRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(dto.getSenha())).thenReturn(SENHA_CRIPTOFRAFADA);

        // 2. ACT - Executa o método a ser testado
        usuarioService.cadastrarUsuario(dto);

        // 3. ASSERT - Verifica os resultados
        // Captura o objeto Usuario passado para o repositório
        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(usuarioCaptor.capture());

        // obtém o usario que foi salvo
        Usuario usuarioSalvo = usuarioCaptor.getValue();

        // verifica se a senha foi criptografada
        assertEquals(SENHA_CRIPTOFRAFADA, usuarioSalvo.getSenha());

        // verifica se os validadores foram chamados
        verify(cpfValidador).isValid(CPF_VALIDO);
        verify(emailValidador).isValid(EMAIL_VALIDO);

    }

    /**
     * Testa impedimento de cadastro com e-mail inválido.
     */
    @Test
    @DisplayName("Nao deve cadastrar se o cpf for invalido")
    void naoDeveCadastrarSeCpfInvalido() {

        String cpfInvalido = "123";
        UsuarioDTO dto = new UsuarioDTO();
        dto.setNome(NOME_VALIDO);
        dto.setCpf(cpfInvalido);
        dto.setEmail(EMAIL_VALIDO);
        dto.setSenha(SENHA);
        dto.setGrupo(Grupo.ADMINISTRADOR);

        when(cpfValidador.isValid(cpfInvalido)).thenReturn(false);

        // ACT e ASSERT
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> usuarioService.cadastrarUsuario(dto));

        assertEquals("CPF inválido", exception.getMessage());
        verify(cpfValidador).isValid(cpfInvalido);

        // Garante que o email nao foi validado e o usuario nao foi salvo
        verifyNoInteractions(emailValidador, usuarioRepository);
    }


    @Test
    @DisplayName("Nao deve cadastrar se o email for invalido")
    void naoDeveCadastrarSeEmailInvalido() {

        String emailInvalido = "email@invalido";
        UsuarioDTO dto = new UsuarioDTO();

        dto.setNome(NOME_VALIDO);
        dto.setCpf(CPF_VALIDO);
        dto.setEmail(emailInvalido);
        dto.setSenha(SENHA);
        dto.setGrupo(Grupo.ADMINISTRADOR);

        when(cpfValidador.isValid(dto.getCpf())).thenReturn(true);
        when(emailValidador.isValid(emailInvalido)).thenReturn(false);

        // ACT e ASSERT
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> usuarioService.cadastrarUsuario(dto));

        assertEquals("Email inválido", exception.getMessage());
        verify(emailValidador).isValid(emailInvalido);

        // Garante que o usuario nao foi salvo
        verify(usuarioRepository, never()).save(any());
    }

    /**
     * Testa se o cadastro de um usuário é impedido
     * quando o email informado já estiver cadastrado no sistema.
     *
     * Espera que uma exceção {@link RuntimeException} seja lançada
     * com a mensagem "Email já cadastrado".
     */
    @Test
    void naoDeveCadastrarSeEmailExistir() {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setNome(NOME_VALIDO);
        dto.setCpf(CPF_VALIDO);
        dto.setEmail(EMAIL_VALIDO);
        dto.setSenha(SENHA);
        dto.setGrupo(Grupo.ADMINISTRADOR);

        when(cpfValidador.isValid(dto.getCpf())).thenReturn(true);
        when(emailValidador.isValid(dto.getEmail())).thenReturn(true);

        // Simula que o email já está cadastrado
        when(usuarioRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(new Usuario()));

        // ACT e ASSERT
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.cadastrarUsuario(dto);
        });

        assertEquals("Email já cadastrado", exception.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    /**
     * Testa listagem de usuários do repositório.
     */
    @Test
    @DisplayName("Deve listar todos os usuarios")
    void deveListarTodosUsuarios() {
        // cria usuarios mockados
        Usuario usuario1 = new Usuario();
        usuario1.setId(1L);
        usuario1.setNome("Usuario 1");

        Usuario usuario2 = new Usuario();
        usuario2.setId(2L);
        usuario2.setNome("Usuario 2");

        List<Usuario> usuariosMock = Arrays.asList(usuario1, usuario2);

        // Configura o mock do repositorio
        when(usuarioRepository.findAll()).thenReturn(usuariosMock);

        // chama o método a ser testado
        List<Usuario> resultado = usuarioService.listarTodosUsuarios();

        // Assert: verifica se o retorno contém os dois usuários simulados
        assertThat(resultado, hasSize(2)); // verifica tamanho da lista
        assertThat(resultado, Matchers.contains(usuario1, usuario2)); // verifica conteúdo exato
        verify(usuarioRepository).findAll(); // verifica chamada ao repositorio
    }

    /**
     * Testa atualização dos dados de um usuário existente.
     */
    @Test
    @DisplayName("Deve atualizar usuario existente")
    void deveAtualizarUsuarioExistente() {
        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setId(ID_USUARIO);
        usuarioExistente.setNome("Nome Antigo");
        usuarioExistente.setCpf("04601759200");
        usuarioExistente.setGrupo(Grupo.ESTOQUISTA);

        UsuarioDTO dtoAtualizado = new UsuarioDTO();
        dtoAtualizado.setNome("Novo Nome");
        dtoAtualizado.setCpf("44268780884");
        dtoAtualizado.setGrupo(Grupo.ADMINISTRADOR);

        when(usuarioRepository.findById(usuarioExistente.getId())).thenReturn(Optional.of(usuarioExistente));

        // ACT
        usuarioService.alterarUsuario(usuarioExistente.getId(), dtoAtualizado);

        // ASSERT
        assertEquals("Novo Nome", usuarioExistente.getNome());
        assertEquals("44268780884", usuarioExistente.getCpf());
        assertEquals(Grupo.ESTOQUISTA, usuarioExistente.getGrupo());
        verify(usuarioRepository).save(usuarioExistente);
    }


    /**
     * Testa erro ao tentar atualizar usuário inexistente.
     */
    @Test
    @DisplayName("Nao deve atualizar usuario inexistente")
    void naoDeveAtualizarUsuarioInexistente() {
        Long idInexistente = 999L;
        when(usuarioRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // ACT e ASSERT
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> usuarioService.alterarUsuario(idInexistente, new UsuarioDTO()));

        assertEquals("Usuário não encontrado", exception.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    /**
     * Testa alternância de status (ativo/inativo) de um usuario.
     */
    @Test
    @DisplayName("Deve alterar o status do usuario")
    void deveAlternarStatusUsuario() {
        Usuario usuario = new Usuario();
        usuario.setId(ID_USUARIO);
        usuario.setAtivo(true);

        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));

        // ACT e ASSERT - Primeira chamada (deve desativar) | (execução)/(verificação)
        usuarioService.habilitarDesabilitarUsuario(usuario.getId());
        assertFalse(usuario.isAtivo());

        // Segunda chamada (deve ativar novamente)
        usuarioService.habilitarDesabilitarUsuario(usuario.getId());
        assertTrue(usuario.isAtivo());

        verify(usuarioRepository, times(2)).save(usuario);
    }

    /**
     * Testa erro ao tentar alterar status de usuário inexistente.
     */
    @Test
    @DisplayName("Nao deve alterar o status de um usuario inexistente")
    void naoDeveAlterarStatusUsuarioInexistente() {
        Long idInexistente = 404L;
        when(usuarioRepository.findById(idInexistente)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> usuarioService.habilitarDesabilitarUsuario(idInexistente));

        assertEquals("Usuário não encontrado", exception.getMessage());
        verify(usuarioRepository, never()).save(any());
    }
}
