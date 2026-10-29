package br.com.fiap.restaurante.core.usecase.impl;

import br.com.fiap.restaurante.core.domain.AppUser;
import br.com.fiap.restaurante.core.dto.UserTypeOutputDto;
import br.com.fiap.restaurante.core.exception.AppUserNotFoundException;
import br.com.fiap.restaurante.core.gateway.AppUserGateway;
import br.com.fiap.restaurante.core.gateway.UserTypeGateway;
import br.com.fiap.restaurante.core.usecase.GetUserTypesByAppUserUseCase;

import java.util.List;

public class GetUserTypesByAppUserUseCaseImpl implements GetUserTypesByAppUserUseCase {

    private final AppUserGateway appUserGateway;
    private final UserTypeGateway userTypeGateway;

    public GetUserTypesByAppUserUseCaseImpl(AppUserGateway appUserGateway, UserTypeGateway userTypeGateway) {
        this.appUserGateway = appUserGateway;
        this.userTypeGateway = userTypeGateway;
    }

    @Override
    public List<UserTypeOutputDto> execute(Long appUserId) {
        AppUser appUser = appUserGateway.findById(appUserId)
                .orElseThrow(() -> new AppUserNotFoundException(appUserId));

        if (appUser.getUserTypeIds().isEmpty()) {
            return List.of();
        }
        return userTypeGateway.findAllById(appUser.getUserTypeIds()).stream()
                .map(UserTypeOutputDto::from)
                .toList();
    }
}
