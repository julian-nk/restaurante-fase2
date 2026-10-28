package br.com.fiap.restaurante.core.domain;

import lombok.Getter;

@Getter
public class UserType {

    static final int NAME_MAX_LENGTH = 50;
    static final int CODE_MAX_LENGTH = 30;

    private final Long id;
    private String name;
    private String code;

    public UserType(Long id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }
}
