package com.velocompra.ecommerce.controller;

import com.velocompra.ecommerce.model.EnderecoEntrega;
import com.velocompra.ecommerce.util.ViaCepClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador responsável pela integração com o serviço ViaCep.
 * Este controlador permite buscar o endereço completo com base no CEP fornecido.
 */
@RestController
@RequestMapping("/api/viacep")
@CrossOrigin(origins = "http://localhost:3000")
public class ViaCepController {

    @Autowired
    private ViaCepClient viaCepClient;

    /**
     * Busca o endereço completo a partir do CEP fornecido.
     * Utiliza o serviço ViaCep para realizar a consulta e retornar os dados do endereço.
     *
     * @param cep O CEP para o qual o endereço será buscado.
     * @return Uma resposta com o endereço encontrado ou uma mensagem de erro caso o CEP seja inválido.
     */
    @GetMapping("/{cep}")
    public ResponseEntity<?> buscar(@PathVariable String cep) {
        try {
            EnderecoEntrega enderecoEntrega = viaCepClient.buscarCep(cep);
            return ResponseEntity.ok(enderecoEntrega); // Retorna o endereço encontrado
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // Retorna erro se a consulta falhar
        }
    }
}
