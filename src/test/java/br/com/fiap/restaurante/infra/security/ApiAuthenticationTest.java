package br.com.fiap.restaurante.infra.security;

import br.com.fiap.restaurante.infra.database.jpa.entity.AppUserEntity;
import br.com.fiap.restaurante.infra.database.jpa.repository.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Chama a API de verdade, com os filtros do Spring Security, para garantir que ela aceita os usuários
 * cadastrados na tabela app_user (via {@link AppUserDetailsService}) e recusa o resto.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ApiAuthenticationTest {

    private static final String PASSWORD = "senhaForte1";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void createUser() {
        appUserRepository.save(new AppUserEntity(null, 1001L, "maria", passwordEncoder.encode(PASSWORD)));
    }

    private static String basic(String login, String password) {
        String credentials = login + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void shouldAcceptRegisteredUser() throws Exception {
        mockMvc.perform(get("/api/user-types").header(HttpHeaders.AUTHORIZATION, basic("maria", PASSWORD)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldAcceptLoginWrittenInAnotherCase() throws Exception {
        mockMvc.perform(get("/api/user-types").header(HttpHeaders.AUTHORIZATION, basic("Maria", PASSWORD)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRejectWrongPassword() throws Exception {
        mockMvc.perform(get("/api/user-types").header(HttpHeaders.AUTHORIZATION, basic("maria", "outraSenha1")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectUnknownUser() throws Exception {
        mockMvc.perform(get("/api/user-types").header(HttpHeaders.AUTHORIZATION, basic("joao", PASSWORD)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectRequestWithoutCredentials() throws Exception {
        mockMvc.perform(get("/api/user-types"))
                .andExpect(status().isUnauthorized());
    }
}
