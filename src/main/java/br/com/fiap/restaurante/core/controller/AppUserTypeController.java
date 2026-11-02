package br.com.fiap.restaurante.core.controller;

import br.com.fiap.restaurante.core.dto.AppUserTypeInputDto;
import br.com.fiap.restaurante.core.dto.UserTypeOutputDto;
import br.com.fiap.restaurante.core.usecase.AssociateUserTypeUseCase;
import br.com.fiap.restaurante.core.usecase.GetUserTypesByAppUserUseCase;
import br.com.fiap.restaurante.core.usecase.RemoveUserTypeFromAppUserUseCase;

import java.util.List;

public class AppUserTypeController {

    private final AssociateUserTypeUseCase associateUserTypeUseCase;
    private final GetUserTypesByAppUserUseCase getUserTypesByAppUserUseCase;
    private final RemoveUserTypeFromAppUserUseCase removeUserTypeFromAppUserUseCase;

    public AppUserTypeController(
            AssociateUserTypeUseCase associateUserTypeUseCase,
            GetUserTypesByAppUserUseCase getUserTypesByAppUserUseCase,
            RemoveUserTypeFromAppUserUseCase removeUserTypeFromAppUserUseCase
    ) {
        this.associateUserTypeUseCase = associateUserTypeUseCase;
        this.getUserTypesByAppUserUseCase = getUserTypesByAppUserUseCase;
        this.removeUserTypeFromAppUserUseCase = removeUserTypeFromAppUserUseCase;
    }

    public void associate(Long appUserId, Long userTypeId) {
        associateUserTypeUseCase.execute(new AppUserTypeInputDto(appUserId, userTypeId));
    }

    public List<UserTypeOutputDto> findUserTypes(Long appUserId) {
        return getUserTypesByAppUserUseCase.execute(appUserId);
    }

    public void remove(Long appUserId, Long userTypeId) {
        removeUserTypeFromAppUserUseCase.execute(new AppUserTypeInputDto(appUserId, userTypeId));
    }
}
