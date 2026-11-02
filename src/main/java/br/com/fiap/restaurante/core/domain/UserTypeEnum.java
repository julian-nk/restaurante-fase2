package br.com.fiap.restaurante.core.domain;

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
