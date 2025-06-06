package com.velocompra.ecommerce.controller;

import com.velocompra.ecommerce.config.SecurityConfig; // Importe a sua classe de configuração de segurança
import com.velocompra.ecommerce.model.EnderecoEntrega;
import com.velocompra.ecommerce.util.ViaCepClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import; // Para importar sua configuração de segurança
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString; // Para qualquer String como argumento
import static org.mockito.Mockito.when; // Para definir o comportamento do mock
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath; // Para verificar o JSON de resposta
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status; // Para verificar o status HTTP

/**
 * Classe de testes para {@link ViaCepController}.
 * Utiliza {@code @WebMvcTest} para focar nos testes da camada web,
 * carregando apenas os beans necessários para o {@code ViaCepController}.
 * Utiliza {@code @MockBean} para simular o comportamento de {@link ViaCepClient}.
 * Importa {@link SecurityConfig} para que as regras de segurança sejam aplicadas.
 */
@WebMvcTest(ViaCepController.class)
@Import(SecurityConfig.class) // Importa a configuração de segurança para permitir acesso ao endpoint
class ViaCepControllerTest {

    @Autowired
    private MockMvc mockMvc; // Objeto para simular requisições HTTP

    @MockitoBean // Cria um mock para ViaCepClient e o injeta no contexto do Spring
    private ViaCepClient viaCepClient;

    /**
     * Testa se o controlador retorna o endereço com status 200 OK para um CEP válido.
     * Simula o ViaCepClient retornando um EnderecoEntrega.
     *
     * @throws Exception Se ocorrer um erro durante a execução da requisição MockMvc.
     */
    @Test
    @DisplayName("Deve retornar 200 OK e o endereço para um CEP válido")
    void deveRetornarEnderecoParaCepValido() throws Exception {
        // Arrange (Preparação)
        String cepValido = "12345678";
        EnderecoEntrega enderecoMock = new EnderecoEntrega();
        enderecoMock.setCep(cepValido);
        enderecoMock.setLogradouro("Rua Teste");
        enderecoMock.setBairro("Bairro Teste");
        enderecoMock.setCidade("Cidade Teste");
        enderecoMock.setUf("TS");

        // Define o comportamento do mock: quando buscarCep for chamado com qualquer String,
        // retorna o enderecoMock.
        when(viaCepClient.buscarCep(anyString())).thenReturn(enderecoMock);

        // Act & Assert (Ação e Verificação)
        mockMvc.perform(get("/api/viacep/{cep}", cepValido)) // Realiza uma requisição GET para a rota de busca
                .andExpect(status().isOk()) // Espera status 200 OK
                // Verifica os campos do JSON retornado
                .andExpect(jsonPath("$.cep").value(cepValido))
                .andExpect(jsonPath("$.logradouro").value("Rua Teste"))
                .andExpect(jsonPath("$.bairro").value("Bairro Teste"))
                .andExpect(jsonPath("$.cidade").value("Cidade Teste"))
                .andExpect(jsonPath("$.uf").value("TS"));
    }

    /**
     * Testa se o controlador retorna 400 Bad Request para um CEP inválido ou não encontrado.
     * Simula o ViaCepClient lançando uma RuntimeException.
     *
     * @throws Exception Se ocorrer um erro durante a execução da requisição MockMvc.
     */
    @Test
    @DisplayName("Deve retornar 400 Bad Request e mensagem de erro para CEP inválido")
    void deveRetornarErroParaCepInvalido() throws Exception {
        // Arrange (Preparação)
        String cepInvalido = "00000000";
        String mensagemErro = "CEP não encontrado ou formato inválido.";

        // Define o comportamento do mock: quando buscarCep for chamado com qualquer String,
        // lança uma RuntimeException com a mensagem de erro.
        when(viaCepClient.buscarCep(anyString())).thenThrow(new RuntimeException(mensagemErro));

        // Act & Assert (Ação e Verificação)
        mockMvc.perform(get("/api/viacep/{cep}", cepInvalido)) // Realiza requisição GET
                .andExpect(status().isBadRequest()) // Espera status 400 Bad Request
                .andExpect(jsonPath("$").value(mensagemErro)); // Verifica se o corpo da resposta contém a mensagem de erro
    }
}