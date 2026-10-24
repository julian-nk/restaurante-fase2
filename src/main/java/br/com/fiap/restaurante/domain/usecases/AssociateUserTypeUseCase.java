package br.com.fiap.restaurante.domain.usecases;

import br.com.fiap.restaurante.domain.entities.UserType;
import br.com.fiap.restaurante.interfaces.IAppUserTypeGateway;
import br.com.fiap.restaurante.interfaces.IUserTypeGateway;

public class AssociateUserTypeUseCase {

    private final IAppUserTypeGateway appUserTypeGateway;
    private final IUserTypeGateway userTypeGateway;

    public AssociateUserTypeUseCase(
            IAppUserTypeGateway appUserTypeGateway,
            IUserTypeGateway userTypeGateway
    ) {
        this.appUserTypeGateway = appUserTypeGateway;
        this.userTypeGateway = userTypeGateway;
    }

    public void execute(Long appUserId, Long userTypeId) {

        if (appUserId == null || appUserId <= 0) {
            throw new IllegalArgumentException(
                    "ID do usuário inválido."
            );
        }

        if (userTypeId == null || userTypeId <= 0) {
            throw new IllegalArgumentException(
                    "ID do tipo de usuário inválido."
            );
        }

        if (!appUserTypeGateway.appUserExists(appUserId)) {
            throw new IllegalArgumentException(
                    "Usuário não encontrado."
            );
        }

        UserType userType = userTypeGateway.findById(userTypeId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Tipo de usuário não encontrado."
                ));

        appUserTypeGateway.associate(
                appUserId,
                userType.getId()
        );
    }
}
