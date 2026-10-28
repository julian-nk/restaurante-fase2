package br.com.fiap.restaurante.core.domain;

import br.com.fiap.restaurante.core.exception.InvalidFieldException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Locale;
import java.util.regex.Pattern;

@Getter
@AllArgsConstructor
public class AppUser {

    static final int LOGIN_MIN_LENGTH = 3;
    static final int LOGIN_MAX_LENGTH = 50;
    private static final Pattern LOGIN_PATTERN = Pattern.compile("^[a-z0-9._-]+$");

    private final Long id;
    private final Long personId;
    private final String login;
    private String passwordHash;

    private static String validateLogin(String login) {
        String value = DomainValidation.requireText(login, LOGIN_MAX_LENGTH, "login").toLowerCase(Locale.ROOT);
        if (value.length() < LOGIN_MIN_LENGTH) {
            throw new InvalidFieldException("O campo login deve ter no mínimo " + LOGIN_MIN_LENGTH + " caracteres.");
        }
        if (!LOGIN_PATTERN.matcher(value).matches()) {
            throw new InvalidFieldException("O login só pode conter letras, números, ponto, hífen e sublinhado.");
        }
        return value;
    }

    public void changePasswordHash(String newPasswordHash) {
        this.passwordHash = DomainValidation.requireText(newPasswordHash, 255, "senha");
    }
}
