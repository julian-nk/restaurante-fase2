package br.com.fiap.restaurante.core.usecase.impl;

import br.com.fiap.restaurante.core.dto.AppUserOutputDto;
import br.com.fiap.restaurante.core.exception.AppUserNotFoundException;
import br.com.fiap.restaurante.core.gateway.AppUserGateway;
import br.com.fiap.restaurante.core.usecase.GetAppUserByIdUseCase;

public class GetAppUserByIdUseCaseImpl implements GetAppUserByIdUseCase {

    private final AppUserGateway appUserGateway;

    public GetAppUserByIdUseCaseImpl(AppUserGateway appUserGateway) {
        this.appUserGateway = appUserGateway;
    }

    @Override
    public AppUserOutputDto execute(Long id) {
        return appUserGateway.findById(id)
                .map(AppUserOutputDto::from)
                .orElseThrow(() -> new AppUserNotFoundException(id));
    }
}
