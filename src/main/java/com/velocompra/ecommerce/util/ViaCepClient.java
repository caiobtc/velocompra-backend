package com.velocompra.ecommerce.util;

import com.velocompra.ecommerce.model.EnderecoEntrega;
import com.velocompra.ecommerce.model.ViaCepResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Componente responsável por interagir com a API ViaCep para buscar informações de endereço a partir de um CEP.
 * Utiliza a API pública ViaCep para recuperar os dados de um endereço com base no CEP informado.
 */
@Component
public class ViaCepClient {

    /**
     * Busca as informações de um endereço a partir de um CEP usando a API ViaCep.
     * A API retorna os dados do endereço em formato JSON, que são então mapeados para um objeto {@link EnderecoEntrega}.
     *
     * @param cep O CEP do endereço a ser buscado.
     * @return Um objeto {@link EnderecoEntrega} contendo as informações do endereço, como logradouro, bairro, cidade e estado.
     * @throws RuntimeException Se o CEP for inválido ou se ocorrer algum erro ao consultar a API ViaCep.
     */
    public EnderecoEntrega buscarCep(String cep) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://viacep.com.br/ws/" + cep + "/json/";

        try {
            // Realiza a requisição HTTP GET para a API ViaCep
            ResponseEntity<ViaCepResponse> response = restTemplate.getForEntity(url, ViaCepResponse.class);
            ViaCepResponse body = response.getBody();

            // Verifica se a resposta é inválida ou se há erro no retorno
            if (body == null || body.getErro() != null) {
                throw new RuntimeException("CEP inválido!");
            }

            // Instancia e popula o objeto EnderecoEntrega usando os dados retornados da API
            EnderecoEntrega enderecoEntrega = new EnderecoEntrega();
            enderecoEntrega.setCep(body.getCep().replaceAll("\\D", ""));  // Remove caracteres não numéricos
            enderecoEntrega.setLogradouro(body.getLogradouro());
            enderecoEntrega.setComplemento(body.getComplemento());
            enderecoEntrega.setBairro(body.getBairro());
            enderecoEntrega.setCidade(body.getLocalidade());
            enderecoEntrega.setUf(body.getUf());

            return enderecoEntrega;
        } catch (Exception e) {
            // Lança uma exceção se ocorrer algum erro durante a requisição ou processamento
            throw new RuntimeException("Erro ao buscar CEP: " + e.getMessage());
        }
    }
}
