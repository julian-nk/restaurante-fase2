package br.com.fiap.restaurante.domain.usecases;

import br.com.fiap.restaurante.domain.entities.UserType;
import br.com.fiap.restaurante.interfaces.IAppUserTypeGateway;

import java.util.List;

public class GetUserTypesByAppUserUseCase {

    private final IAppUserTypeGateway appUserTypeGateway;

    public GetUserTypesByAppUserUseCase(
            IAppUserTypeGateway appUserTypeGateway
    ) {
        this.appUserTypeGateway = appUserTypeGateway;
    }

    public List<UserType> execute(Long appUserId) {
        if (appUserId == null || appUserId <= 0) {
            throw new IllegalArgumentException(
                    "ID do usuário inválido."
            );
        }

        if (!appUserTypeGateway.appUserExists(appUserId)) {
            throw new IllegalArgumentException(
                    "Usuário não encontrado."
            );
        }

        return appUserTypeGateway.findUserTypesByAppUserId(appUserId);
    }
}