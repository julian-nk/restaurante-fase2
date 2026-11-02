package br.com.fiap.restaurante.core.dto;

public record LoginInputDto(String login, String password) {

    /** Se nem o login nem a senha foram informados. */
    public boolean isEmpty() {
        return isBlank(login) && isBlank(password);
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
