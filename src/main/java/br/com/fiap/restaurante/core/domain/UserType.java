package br.com.fiap.restaurante.core.domain;

import br.com.fiap.restaurante.core.exception.InvalidFieldException;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserType {

    static final int NAME_MAX_LENGTH = 50;
    static final int CODE_MAX_LENGTH = 30;

    private final Long id;
    private String name;
    private String code;

    @Builder
    public UserType(Long id, String name, String code) {
        this.id = id;
        this.name = requireValue(name, NAME_MAX_LENGTH, "nome do tipo de usuário");
        this.code = requireValue(code, CODE_MAX_LENGTH, "código do tipo de usuário");
    }

    public void update(String name, String code) {
        String validName = requireValue(name, NAME_MAX_LENGTH, "nome do tipo de usuário");
        String validCode = requireValue(code, CODE_MAX_LENGTH, "código do tipo de usuário");
        this.name = validName;
        this.code = validCode;
    }

    private static String requireValue(String value, int maxLength, String label) {
        if (value == null || value.isBlank()) {
            throw new InvalidFieldException("O " + label + " é obrigatório.");
        }
        String trimmed = value.trim();
        if (trimmed.length() > maxLength) {
            throw new InvalidFieldException("O " + label + " deve ter no máximo " + maxLength + " caracteres.");
        }
        return trimmed;
    }
}
