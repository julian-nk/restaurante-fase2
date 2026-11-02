package br.com.fiap.restaurante.infra.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BCryptPasswordEncoderGatewayTest {

    private final BCryptPasswordEncoderGateway encoder = new BCryptPasswordEncoderGateway();

    @Test
    void shouldHashAndVerifyPassword() {
        String hash = encoder.encode("senhaForte1");

        assertNotEquals("senhaForte1", hash);
        assertTrue(hash.startsWith("$2"));
        assertTrue(encoder.matches("senhaForte1", hash));
        assertFalse(encoder.matches("outraSenha1", hash));
    }
}
