package br.com.fiap.restaurante.infra.config;

import br.com.fiap.restaurante.infra.security.ApiAccessRules;
import br.com.fiap.restaurante.infra.security.JsonSecurityErrorHandler;
import br.com.fiap.restaurante.infra.security.Rule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
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
        JsonSecurityErrorHandler errorHandler = new JsonSecurityErrorHandler();

        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(ApiAccessRules.PUBLIC_PATHS.toArray(String[]::new)).permitAll();

                    for (Rule rule : ApiAccessRules.RULES) {
                        var route = rule.method() == null
                                    ? auth.requestMatchers(rule.pattern())
                                    : auth.requestMatchers(rule.method(), rule.pattern());
                        if (rule.allowsAnyAuthenticatedUser()) {
                            route.authenticated();
                        } else {
                            route.hasAnyRole(rule.roles());
                        }
                    }

                    auth.anyRequest().authenticated();
                })
                .httpBasic(basic -> basic.authenticationEntryPoint(errorHandler))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(errorHandler)
                        .accessDeniedHandler(errorHandler));

        return http.build();
    }
}
