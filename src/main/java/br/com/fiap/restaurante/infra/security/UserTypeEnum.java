package br.com.fiap.restaurante.infra.security;

import lombok.Getter;

@Getter
public enum UserTypeEnum {

    ADMIN("admin"),
    RESTAURANT_OWNER("restaurant_owner"),
    CUSTOMER("customer");

    private final String code;

    UserTypeEnum(String code) {
        this.code = code;
    }

}
