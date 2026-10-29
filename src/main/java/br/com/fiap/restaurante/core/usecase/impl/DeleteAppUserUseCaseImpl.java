package br.com.fiap.restaurante.core.usecase.impl;

import br.com.fiap.restaurante.core.exception.AppUserNotFoundException;
import br.com.fiap.restaurante.core.gateway.AppUserGateway;
import br.com.fiap.restaurante.core.usecase.DeleteAppUserUseCase;

public class DeleteAppUserUseCaseImpl implements DeleteAppUserUseCase {

    private final AppUserGateway appUserGateway;

    public DeleteAppUserUseCaseImpl(AppUserGateway appUserGateway) {
        this.appUserGateway = appUserGateway;
    }

    @Override
    public void execute(Long id) {
        if (!appUserGateway.existsById(id)) {
            throw new AppUserNotFoundException(id);
        }
        appUserGateway.deleteById(id);
    }
}
