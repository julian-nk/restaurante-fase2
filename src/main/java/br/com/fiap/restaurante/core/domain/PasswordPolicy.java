package br.com.fiap.restaurante.core.domain;

import br.com.fiap.restaurante.core.exception.InvalidFieldException;
import org.apache.commons.lang3.StringUtils;

public final class PasswordPolicy {

    static final int MIN_LENGTH = 8;
    //Limite do BCrypt
    static final int MAX_LENGTH = 72;

    private PasswordPolicy() {
    }

    public static void validate(String rawPassword) {
        if (StringUtils.isBlank(rawPassword)) {
            throw new InvalidFieldException("A senha é obrigatória.");
        }
        if (rawPassword.length() < MIN_LENGTH || rawPassword.length() > MAX_LENGTH) {
            throw new InvalidFieldException(
                    "A senha deve ter entre " + MIN_LENGTH + " e " + MAX_LENGTH + " caracteres.");
        }
        boolean hasLetter = rawPassword.chars().anyMatch(Character::isLetter);
        boolean hasDigit = rawPassword.chars().anyMatch(Character::isDigit);
        if (!hasLetter || !hasDigit) {
            throw new InvalidFieldException("A senha deve conter letras e números.");
        }
    }
}
