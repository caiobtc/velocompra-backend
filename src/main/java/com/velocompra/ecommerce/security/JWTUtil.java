package com.velocompra.ecommerce.security;

import com.velocompra.ecommerce.model.Cliente;
import com.velocompra.ecommerce.model.Usuario;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

/**
 * Classe utilitária para manipulação de tokens JWT.
 * Esta classe fornece métodos para gerar tokens JWT para usuários e clientes,
 * assinados com uma chave secreta e com um tempo de expiração configurável.
 */
@Component
public class JWTUtil {

    private final Key SECRET_KEY;
    private final long expiration;

    /**
     * Construtor para inicializar a chave secreta e o tempo de expiração.
     * @param secret A chave secreta utilizada para assinar o token JWT, fornecida via configuração.
     * @param expiration O tempo de expiração do token em milissegundos, fornecido via configuração.
     */
    public JWTUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration}") long expiration) {
        this.SECRET_KEY = new SecretKeySpec(Base64.getDecoder().decode(secret), SignatureAlgorithm.HS512.getJcaName());
        this.expiration = expiration;
    }

    /**
     * Gera um token JWT para um usuário.
     * O token contém o e-mail do usuário como o assunto e o grupo de acesso como uma reivindicação.
     * O token é assinado com a chave secreta e tem um tempo de expiração configurado.
     *
     * @param usuario O usuário para o qual o token será gerado.
     * @return O token JWT gerado.
     */
    public String generateToken(Usuario usuario) {
        return Jwts.builder()
                .setSubject(usuario.getEmail()) // O e-mail do usuário como o sujeito do token.
                .claim("grupo", usuario.getGrupo().name()) // O grupo de acesso do usuário (ex: ADMINISTRADOR, CLIENTE).
                .setIssuedAt(new Date()) // Define a data e hora em que o token foi emitido.
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // Define o tempo de expiração do token.
                .signWith(SECRET_KEY, SignatureAlgorithm.HS512) // Assina o token com a chave secreta.
                .compact(); // Gera o token compactado.
    }

    /**
     * Gera um token JWT para um cliente.
     * O token contém o e-mail do cliente como o assunto e um grupo fixo de "CLIENTE" como uma reivindicação.
     * O token é assinado com a chave secreta e tem um tempo de expiração configurado.
     *
     * @param cliente O cliente para o qual o token será gerado.
     * @return O token JWT gerado.
     */
    public String generateTokenCliente(Cliente cliente) {
        return Jwts.builder()
                .setSubject(cliente.getEmail()) // O e-mail do cliente como o sujeito do token.
                .claim("grupo", "CLIENTE") // Define o grupo do cliente como "CLIENTE".
                .setIssuedAt(new Date()) // Define a data e hora em que o token foi emitido.
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // Define o tempo de expiração do token.
                .signWith(SECRET_KEY, SignatureAlgorithm.HS512) // Assina o token com a chave secreta.
                .compact(); // Gera o token compactado.
    }
}
