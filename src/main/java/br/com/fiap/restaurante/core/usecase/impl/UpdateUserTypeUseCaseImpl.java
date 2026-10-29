package br.com.fiap.restaurante.core.usecase.impl;

import br.com.fiap.restaurante.core.domain.UserType;
import br.com.fiap.restaurante.core.dto.UpdateUserTypeInputDto;
import br.com.fiap.restaurante.core.dto.UserTypeOutputDto;
import br.com.fiap.restaurante.core.exception.UserTypeAlreadyExistsException;
import br.com.fiap.restaurante.core.exception.UserTypeNotFoundException;
import br.com.fiap.restaurante.core.gateway.UserTypeGateway;
import br.com.fiap.restaurante.core.usecase.UpdateUserTypeUseCase;

public class UpdateUserTypeUseCaseImpl implements UpdateUserTypeUseCase {

    private final UserTypeGateway userTypeGateway;

    public UpdateUserTypeUseCaseImpl(UserTypeGateway userTypeGateway) {
        this.userTypeGateway = userTypeGateway;
    }

    @Override
    public UserTypeOutputDto execute(UpdateUserTypeInputDto input) {
        UserType userType = userTypeGateway.findById(input.id())
                .orElseThrow(() -> new UserTypeNotFoundException(input.id()));

        String previousCode = userType.getCode();
        userType.update(input.name(), input.code());

        if (!userType.getCode().equals(previousCode) && userTypeGateway.existsByCode(userType.getCode())) {
            throw new UserTypeAlreadyExistsException(userType.getCode());
        }

        return UserTypeOutputDto.from(userTypeGateway.save(userType));
    }
}
