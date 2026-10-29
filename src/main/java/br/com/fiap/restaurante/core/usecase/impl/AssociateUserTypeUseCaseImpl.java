package br.com.fiap.restaurante.core.usecase.impl;

import br.com.fiap.restaurante.core.dto.AppUserTypeInputDto;
import br.com.fiap.restaurante.core.exception.AppUserNotFoundException;
import br.com.fiap.restaurante.core.exception.UserTypeNotFoundException;
import br.com.fiap.restaurante.core.gateway.AppUserGateway;
import br.com.fiap.restaurante.core.gateway.AppUserTypeGateway;
import br.com.fiap.restaurante.core.gateway.UserTypeGateway;
import br.com.fiap.restaurante.core.usecase.AssociateUserTypeUseCase;

public class AssociateUserTypeUseCaseImpl implements AssociateUserTypeUseCase {

    private final AppUserTypeGateway appUserTypeGateway;
    private final AppUserGateway appUserGateway;
    private final UserTypeGateway userTypeGateway;

    public AssociateUserTypeUseCaseImpl(
            AppUserTypeGateway appUserTypeGateway,
            AppUserGateway appUserGateway,
            UserTypeGateway userTypeGateway
    ) {
        this.appUserTypeGateway = appUserTypeGateway;
        this.appUserGateway = appUserGateway;
        this.userTypeGateway = userTypeGateway;
    }

    @Override
    public void execute(AppUserTypeInputDto input) {

        if (!appUserGateway.existsById(input.appUserId())) {
            throw new AppUserNotFoundException(input.appUserId());
        }

        if (userTypeGateway.findById(input.userTypeId()).isEmpty()) {
            throw new UserTypeNotFoundException(input.userTypeId());
        }

        appUserTypeGateway.associate(input.appUserId(), input.userTypeId());
    }
}
