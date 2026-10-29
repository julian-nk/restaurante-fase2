package br.com.fiap.restaurante.core.usecase.impl;

import br.com.fiap.restaurante.core.dto.UserTypeOutputDto;
import br.com.fiap.restaurante.core.exception.AppUserNotFoundException;
import br.com.fiap.restaurante.core.gateway.AppUserGateway;
import br.com.fiap.restaurante.core.gateway.AppUserTypeGateway;
import br.com.fiap.restaurante.core.usecase.GetUserTypesByAppUserUseCase;

import java.util.List;

public class GetUserTypesByAppUserUseCaseImpl implements GetUserTypesByAppUserUseCase {

    private final AppUserTypeGateway appUserTypeGateway;
    private final AppUserGateway appUserGateway;

    public GetUserTypesByAppUserUseCaseImpl(
            AppUserTypeGateway appUserTypeGateway,
            AppUserGateway appUserGateway
    ) {
        this.appUserTypeGateway = appUserTypeGateway;
        this.appUserGateway = appUserGateway;
    }

    @Override
    public List<UserTypeOutputDto> execute(Long appUserId) {

        if (!appUserGateway.existsById(appUserId)) {
            throw new AppUserNotFoundException(appUserId);
        }

        return appUserTypeGateway.findUserTypesByAppUserId(appUserId)
                                 .stream()
                                 .map(UserTypeOutputDto::from)
                                 .toList();
    }
}
