package br.com.fiap.restaurante.core.usecase.impl;

import br.com.fiap.restaurante.core.domain.UserType;
import br.com.fiap.restaurante.core.dto.CreateUserTypeInputDto;
import br.com.fiap.restaurante.core.dto.UserTypeOutputDto;
import br.com.fiap.restaurante.core.exception.UserTypeAlreadyExistsException;
import br.com.fiap.restaurante.core.factory.UserTypeFactory;
import br.com.fiap.restaurante.core.gateway.UserTypeGateway;
import br.com.fiap.restaurante.core.usecase.CreateUserTypeUseCase;

public class CreateUserTypeUseCaseImpl implements CreateUserTypeUseCase {

    private final UserTypeGateway userTypeGateway;
    private final UserTypeFactory userTypeFactory;

    public CreateUserTypeUseCaseImpl(UserTypeGateway userTypeGateway, UserTypeFactory userTypeFactory) {
        this.userTypeGateway = userTypeGateway;
        this.userTypeFactory = userTypeFactory;
    }

    @Override
    public UserTypeOutputDto execute(CreateUserTypeInputDto input) {
        UserType userType = userTypeFactory.create(input);

        if (userTypeGateway.existsByCode(userType.getCode())) {
            throw new UserTypeAlreadyExistsException(userType.getCode());
        }

        return UserTypeOutputDto.from(userTypeGateway.save(userType));
    }
}
