package com.velocompra.ecommerce.util;
import com.velocompra.ecommerce.model.Endereco;
import com.velocompra.ecommerce.model.ViaCepResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ViaCepClient {


    public Endereco buscarCep(String cep) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://viacep.com.br/ws/" + cep + "/json/";

        try {
            ResponseEntity<ViaCepResponse> response = restTemplate.getForEntity(url, ViaCepResponse.class);
            ViaCepResponse body = response.getBody();

            if (body == null || body.getErro() != null) {
                throw new RuntimeException("CEP inválido!");
            }

            // ✅ Instancia e popula usando setters (construtor vazio)
            Endereco endereco = new Endereco();
            endereco.setCep(body.getCep().replaceAll("\\D", ""));
            endereco.setLogradouro(body.getLogradouro());
            endereco.setComplemento(body.getComplemento());
            endereco.setBairro(body.getBairro());
            endereco.setCidade(body.getLocalidade());
            endereco.setUf(body.getUf());

            return endereco;        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar CEP: " + e.getMessage());
        }
    }
}
