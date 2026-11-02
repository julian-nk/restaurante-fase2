package br.com.fiap.restaurante.core.factory.impl;

import br.com.fiap.restaurante.core.domain.AppUser;
import br.com.fiap.restaurante.core.dto.CreateAppUserInputDto;
import br.com.fiap.restaurante.core.factory.AppUserFactory;

import java.util.Set;

public class AppUserFactoryImpl implements AppUserFactory {

    @Override
    public AppUser create(CreateAppUserInputDto input, String passwordHash) {
        return AppUser.builder()
                .personId(input.personId())
                .login(input.login())
                .passwordHash(passwordHash)
                .build();
    }

    @Override
    public AppUser restore(Long id, Long personId, String login, String passwordHash, Set<Long> userTypeIds) {
        return AppUser.builder()
                .id(id)
                .personId(personId)
                .login(login)
                .passwordHash(passwordHash)
                .userTypeIds(userTypeIds)
                .build();
    }
}
