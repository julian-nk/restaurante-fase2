package br.com.fiap.restaurante.core.dto;

import br.com.fiap.restaurante.core.domain.UserType;

public record UserTypeOutputDto(Long id, String name, String code) {

    public static UserTypeOutputDto from(UserType userType) {
        return new UserTypeOutputDto(userType.getId(), userType.getName(), userType.getCode());
    }
}
