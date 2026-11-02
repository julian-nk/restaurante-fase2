package br.com.fiap.restaurante.core.domain;

import br.com.fiap.restaurante.core.exception.InvalidFieldException;

import java.util.Locale;
import java.util.regex.Pattern;

public final class DomainValidation {

    static final int EMAIL_MAX_LENGTH = 255;

    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final Pattern DOCUMENT_SEPARATORS = Pattern.compile("[.\\-/\\s]");

    private DomainValidation() {
    }

    static String requireText(String value, int maxLength, String field) {
        if (value == null || value.isBlank()) {
            throw new InvalidFieldException("O campo " + field + " é obrigatório.");
        }
        return checkLength(value.trim(), maxLength, field);
    }

    static String optionalText(String value, int maxLength, String field) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return checkLength(value.trim(), maxLength, field);
    }

    /** Documento numérico (CPF, CNPJ, CEP): aceita pontos, traços, barras e espaços, e devolve só os dígitos. */
    static String requireDigits(String value, int length, String field) {
        if (value == null || value.isBlank()) {
            throw new InvalidFieldException("O campo " + field + " é obrigatório.");
        }
        String digits = DOCUMENT_SEPARATORS.matcher(value).replaceAll("");
        if (digits.length() != length || !digits.chars().allMatch(Character::isDigit)) {
            throw new InvalidFieldException("O campo " + field + " deve conter " + length + " dígitos.");
        }
        return digits;
    }

    /** E-mail obrigatório, normalizado em minúsculas. */
    static String requireEmail(String value, String field) {
        String email = requireText(value, EMAIL_MAX_LENGTH, field).toLowerCase(Locale.ROOT);
        if (!EMAIL.matcher(email).matches()) {
            throw new InvalidFieldException("O campo " + field + " deve ser um e-mail válido.");
        }
        return email;
    }

    static Long requirePositive(Long id, String field) {
        if (id == null || id <= 0) {
            throw new InvalidFieldException("O campo " + field + " deve ser maior que zero.");
        }
        return id;
    }

    static <T> T requireNonNull(T value, String field) {
        if (value == null) {
            throw new InvalidFieldException("O campo " + field + " é obrigatório.");
        }
        return value;
    }

    private static String checkLength(String value, int maxLength, String field) {
        if (value.length() > maxLength) {
            throw new InvalidFieldException(
                    "O campo " + field + " deve ter no máximo " + maxLength + " caracteres.");
        }
        return value;
    }
}
