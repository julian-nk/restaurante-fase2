package br.com.fiap.restaurante.core.factory.impl;

import br.com.fiap.restaurante.core.domain.UserType;
import br.com.fiap.restaurante.core.dto.CreateUserTypeInputDto;
import br.com.fiap.restaurante.core.factory.UserTypeFactory;

public class UserTypeFactoryImpl implements UserTypeFactory {

    @Override
    public UserType create(CreateUserTypeInputDto input) {
        return UserType.builder()
                .name(input.name())
                .code(input.code())
                .build();
    }

    @Override
    public UserType restore(Long id, String name, String code) {
        return UserType.builder()
                .id(id)
                .name(name)
                .code(code)
                .build();
    }
}
