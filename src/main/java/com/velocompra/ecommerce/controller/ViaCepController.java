package com.velocompra.ecommerce.controller;

import com.velocompra.ecommerce.model.Endereco;
import com.velocompra.ecommerce.util.ViaCepClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/viacep")
@CrossOrigin(origins = "http://localhost:3000")
public class ViaCepController {

    @Autowired
    private ViaCepClient viaCepClient;

    @GetMapping("/{cep}")
    public ResponseEntity<?> buscar(@PathVariable String cep) {
        try {
            Endereco endereco = viaCepClient.buscarCep(cep);
            return ResponseEntity.ok(endereco);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
