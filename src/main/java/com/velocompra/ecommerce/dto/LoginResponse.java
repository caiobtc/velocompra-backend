package com.velocompra.ecommerce.dto;

import lombok.Data;

@Data
public class LoginResponse {

    private String token;
    private String nome;
    private String grupo;

    public LoginResponse(String token, String nome, String grupo) {
        this.token = token;
        this.nome = nome;
        this.grupo = grupo;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }
}
