package br.com.fiap.restaurante.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    /**
     * As senhas são gravadas em BCrypt sem o prefixo {@code {bcrypt}}, então o Spring Security precisa saber
     * que o formato é esse para compará-las.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return deactivateAuthentication(http);
    }

    private SecurityFilterChain deactivateAuthentication(HttpSecurity http) {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
    }

    /**
     * Todas as rotas da API exigem HTTP Basic com o login e a senha de um usuário cadastrado (ver
     * {@link br.com.fiap.restaurante.infra.security.AppUserDetailsService}); só a documentação fica aberta.
     */
    private SecurityFilterChain activateAuthentication(HttpSecurity http) {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/my-docs",
                                "/my-docs/**",
                                "/api-docs",
                                "/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }
}
