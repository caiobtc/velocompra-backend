package com.velocompra.ecommerce.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Controlador responsável pela manipulação de uploads de arquivos.
 * Este controlador permite servir imagens armazenadas no servidor com base no nome do arquivo.
 */
@RestController
@RequestMapping("/uploads")
@CrossOrigin(origins = "http://localhost:3000")
public class UploadController {

    @Value("${produto.upload-dir:uploads/}")
    private String uploadDir;

    /**
     * Serve um arquivo de imagem com base no nome fornecido.
     * O arquivo é recuperado do diretório de upload e retornado como uma resposta para visualização no cliente.
     *
     * @param filename O nome do arquivo de imagem a ser recuperado.
     * @return Uma resposta contendo o arquivo de imagem, ou uma resposta de erro se o arquivo não for encontrado ou não for legível.
     */
    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            // Resolve o caminho do arquivo com base no nome fornecido
            Path file = Paths.get(uploadDir).resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            // Verifica se o recurso existe e é legível
            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"") // Configura o cabeçalho de disposição de conteúdo
                        .body(resource); // Retorna o arquivo como corpo da resposta
            } else {
                return ResponseEntity.notFound().build(); // Retorna 404 se o arquivo não for encontrado ou não for legível
            }

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build(); // Retorna erro interno se algo falhar
        }
    }

}
