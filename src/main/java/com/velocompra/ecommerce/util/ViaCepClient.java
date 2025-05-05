package com.velocompra.ecommerce.util;
import com.velocompra.ecommerce.model.EnderecoEntrega;
import com.velocompra.ecommerce.model.ViaCepResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ViaCepClient {


    public EnderecoEntrega buscarCep(String cep) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://viacep.com.br/ws/" + cep + "/json/";

        try {
            ResponseEntity<ViaCepResponse> response = restTemplate.getForEntity(url, ViaCepResponse.class);
            ViaCepResponse body = response.getBody();

            if (body == null || body.getErro() != null) {
                throw new RuntimeException("CEP inválido!");
            }

            // ✅ Instancia e popula usando setters (construtor vazio)
            EnderecoEntrega enderecoEntrega = new EnderecoEntrega();
            enderecoEntrega.setCep(body.getCep().replaceAll("\\D", ""));
            enderecoEntrega.setLogradouro(body.getLogradouro());
            enderecoEntrega.setComplemento(body.getComplemento());
            enderecoEntrega.setBairro(body.getBairro());
            enderecoEntrega.setCidade(body.getLocalidade());
            enderecoEntrega.setUf(body.getUf());

            return enderecoEntrega;        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar CEP: " + e.getMessage());
        }
    }
}
