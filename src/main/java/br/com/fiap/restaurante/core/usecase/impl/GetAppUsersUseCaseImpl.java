package br.com.fiap.restaurante.core.usecase.impl;

import br.com.fiap.restaurante.core.dto.AppUserOutputDto;
import br.com.fiap.restaurante.core.gateway.AppUserGateway;
import br.com.fiap.restaurante.core.usecase.GetAppUsersUseCase;

import java.util.List;

public class GetAppUsersUseCaseImpl implements GetAppUsersUseCase {

    private final AppUserGateway appUserGateway;

    public GetAppUsersUseCaseImpl(AppUserGateway appUserGateway) {
        this.appUserGateway = appUserGateway;
    }

    @Override
    public List<AppUserOutputDto> execute() {
        return appUserGateway.findAll().stream().map(AppUserOutputDto::from).toList();
    }
}
