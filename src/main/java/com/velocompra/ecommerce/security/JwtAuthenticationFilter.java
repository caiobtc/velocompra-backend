package com.velocompra.ecommerce.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.Key;
import java.util.Base64;
import java.util.Collections;

/**
 * Filtro para autenticação JWT.
 * Este filtro verifica a presença e validade do token JWT no cabeçalho Authorization.
 * Se o token for válido, configura a autenticação do Spring Security para o usuário.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final Key SECRET_KEY;

    /**
     * Construtor que inicializa a chave secreta com base no segredo fornecido.
     * @param secret O segredo utilizado para assinar e validar o token JWT.
     */
    public JwtAuthenticationFilter(@Value("${jwt.secret}") String secret) {
        this.SECRET_KEY = new SecretKeySpec(Base64.getDecoder().decode(secret), SignatureAlgorithm.HS512.getJcaName());
    }

    /**
     * Método principal que realiza a filtragem da requisição.
     * Verifica o token JWT no cabeçalho Authorization, valida-o e configura a autenticação se válido.
     * @param request A requisição HTTP recebida.
     * @param response A resposta HTTP a ser retornada.
     * @param filterChain O encadeamento do filtro para continuar o processamento da requisição.
     * @throws ServletException Se ocorrer um erro durante o processamento da requisição.
     * @throws IOException Se ocorrer um erro de E/S ao lidar com a requisição ou resposta.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = recuperarToken(request);

        if (token != null) {
            try {
                // Valida e extrai informações do token
                Claims claims = Jwts.parser()
                        .setSigningKey(SECRET_KEY)
                        .parseClaimsJws(token)
                        .getBody();

                String email = claims.getSubject(); // Subject = e-mail do usuário
                String grupo = claims.get("grupo", String.class); // Perfil de acesso (ADMINISTRADOR/ESTOQUISTA/CLIENTE)
                logger.info("Grupo Extraido do token: " + grupo);
                logger.info("Email Extraido do token: " + claims.getSubject());

                if (email != null && grupo != null) {
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority(grupo);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(email, null, Collections.singletonList(authority));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.info("Authentication set in SecurityContext: " + SecurityContextHolder.getContext().getAuthentication());
                }

            } catch (ExpiredJwtException e) {
                logger.warn("Token expirado: ", e);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token expirado.");
                return;

            } catch (UnsupportedJwtException e) {
                logger.warn("Token não suportado: ", e);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token não suportado.");
                return;

            } catch (MalformedJwtException e) {
                logger.warn("Token mal formado: ", e);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token inválido.");
                return;

            } catch (SignatureException e) {
                logger.warn("Assinatura inválida: ", e);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Assinatura do token inválida.");
                return;

            } catch (IllegalArgumentException e) {
                logger.warn("Token ausente ou vazio: ", e);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Token não fornecido.");
                return;

            } catch (Exception e) {
                logger.error("Erro inesperado na autenticação JWT: ", e);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Erro interno na autenticação.");
                return;
            }
        }

        // Continua a requisição normalmente se não houver token ou ele for válido
        filterChain.doFilter(request, response);
    }

    /**
     * Extrai o token JWT do cabeçalho Authorization (formato Bearer {token}).
     * @param request A requisição HTTP recebida.
     * @return O token JWT extraído, ou null se não estiver presente no cabeçalho.
     */
    private String recuperarToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        return null;
    }
}
