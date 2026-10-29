package br.com.fiap.restaurante.core.usecase.impl;

import br.com.fiap.restaurante.core.domain.AppUser;
import br.com.fiap.restaurante.core.dto.AppUserTypeInputDto;
import br.com.fiap.restaurante.core.exception.AppUserNotFoundException;
import br.com.fiap.restaurante.core.exception.UserTypeNotFoundException;
import br.com.fiap.restaurante.core.gateway.AppUserGateway;
import br.com.fiap.restaurante.core.gateway.UserTypeGateway;
import br.com.fiap.restaurante.core.usecase.RemoveUserTypeFromAppUserUseCase;

/** Tira um tipo do usuário. Se ele não tiver o tipo, nada é gravado e não há erro. */
public class RemoveUserTypeFromAppUserUseCaseImpl implements RemoveUserTypeFromAppUserUseCase {

    private final AppUserGateway appUserGateway;
    private final UserTypeGateway userTypeGateway;

    public RemoveUserTypeFromAppUserUseCaseImpl(AppUserGateway appUserGateway, UserTypeGateway userTypeGateway) {
        this.appUserGateway = appUserGateway;
        this.userTypeGateway = userTypeGateway;
    }

    @Override
    public void execute(AppUserTypeInputDto input) {
        AppUser appUser = appUserGateway.findById(input.appUserId())
                .orElseThrow(() -> new AppUserNotFoundException(input.appUserId()));

        if (userTypeGateway.findById(input.userTypeId()).isEmpty()) {
            throw new UserTypeNotFoundException(input.userTypeId());
        }

        if (appUser.removeUserType(input.userTypeId())) {
            appUserGateway.save(appUser);
        }
    }
}
