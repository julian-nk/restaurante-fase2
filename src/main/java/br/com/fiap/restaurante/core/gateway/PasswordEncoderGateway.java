package br.com.fiap.restaurante.core.gateway;

/**
 * Geração e verificação de hash de senha. A implementação (ex.: BCrypt) fica na infra.
 */
public interface PasswordEncoderGateway {

    String encode(String rawPassword);

    boolean matches(String rawPassword, String passwordHash);
}
