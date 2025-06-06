package com.velocompra.ecommerce.config;

import com.velocompra.ecommerce.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Classe de configuração de segurança para o sistema.
 * Esta classe configura as permissões de acesso, autenticação, CORS, e o filtro JWT.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Habilita a segurança baseada em anotações (@PreAuthorize)
public class SecurityConfig {

    /**
     * Cria um bean de codificador de senha utilizando o algoritmo BCrypt.
     * Este codificador é utilizado para encriptar senhas de usuários.
     *
     * @return O codificador de senha BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Cria um bean do filtro de autenticação JWT.
     * O filtro JWT é responsável por validar o token JWT presente no cabeçalho da requisição.
     *
     * @param secret A chave secreta utilizada para validar os tokens JWT.
     * @return O filtro de autenticação JWT.
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(@Value("${jwt.secret}") String secret) {
        return new JwtAuthenticationFilter(secret);
    }

    /**
     * Configura o filtro de segurança da aplicação, incluindo CORS, CSRF, controle de sessão e permissões de acesso.
     * O filtro também adiciona o filtro de autenticação JWT para validar tokens de autenticação.
     *
     * @param http A configuração de segurança HTTP.
     * @param jwtAuthenticationFilter O filtro de autenticação JWT.
     * @return A configuração do filtro de segurança.
     * @throws Exception Se ocorrer algum erro ao configurar a segurança.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Configura CORS
                .csrf(csrf -> csrf.disable()) // Desabilita a proteção CSRF
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Define a criação de sessão como stateless
                .authorizeHttpRequests(auth -> auth
                        // Públicos
                        .requestMatchers("/api/auth/**", "/uploads/**").permitAll()
                        .requestMatchers("/api/produtos", "/api/produtos/", "/api/produtos/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll()
                        .requestMatchers("/api/clientes/cadastrar").permitAll()
                        .requestMatchers("/api/viacep/**").permitAll()

                        // Exclusivos do CLIENTE
                        .requestMatchers("/api/pedidos/**").hasAuthority("CLIENTE")
                        .requestMatchers("/api/clientes/meus-dados").hasAuthority("CLIENTE")
                        .requestMatchers("/api/carrinho/**").hasAuthority("CLIENTE")
                        .requestMatchers("/api/checkout/**").hasAuthority("CLIENTE")
                        .requestMatchers("/api/pedidos/meus-pedidos").hasAuthority("CLIENTE")

                        // Privados (Admin / Estoquista)
                        .requestMatchers("/api/produtos/admin/**").hasAnyAuthority("ADMINISTRADOR", "ESTOQUISTA")
                        .requestMatchers("/api/produtos/{id}/status").hasAuthority("ADMINISTRADOR")
                        .requestMatchers("/api/produtos/{id}/estoque").hasAuthority("ESTOQUISTA")
                        .requestMatchers("/api/usuarios/**").hasAuthority("ADMINISTRADOR")
                        .requestMatchers("/api/admin/pedidos").hasAuthority("ESTOQUISTA")
                        .anyRequest().authenticated() // Requer autenticação para qualquer outra requisição
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // Adiciona o filtro JWT antes do filtro de autenticação do Spring Security

        return http.build();
    }

    /**
     * Configura a política de CORS (Cross-Origin Resource Sharing) para permitir requisições de origens específicas.
     * A configuração define os métodos e cabeçalhos permitidos nas requisições.
     *
     * @return A configuração de CORS.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Configura as origens permitidas
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        // Configura os métodos permitidos
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        // Configura os cabeçalhos permitidos
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true); // Permite enviar credenciais (cookies, cabeçalhos de autorização, etc.)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Aplica a configuração a todas as rotas

        return source;
    }
}
