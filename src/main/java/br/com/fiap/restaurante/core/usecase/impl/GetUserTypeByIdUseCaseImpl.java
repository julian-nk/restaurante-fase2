package br.com.fiap.restaurante.core.usecase.impl;

import br.com.fiap.restaurante.core.dto.UserTypeOutputDto;
import br.com.fiap.restaurante.core.exception.UserTypeNotFoundException;
import br.com.fiap.restaurante.core.gateway.UserTypeGateway;
import br.com.fiap.restaurante.core.usecase.GetUserTypeByIdUseCase;

public class GetUserTypeByIdUseCaseImpl implements GetUserTypeByIdUseCase {

    private final UserTypeGateway userTypeGateway;

    public GetUserTypeByIdUseCaseImpl(UserTypeGateway userTypeGateway) {
        this.userTypeGateway = userTypeGateway;
    }

    @Override
    public UserTypeOutputDto execute(Long id) {

        return userTypeGateway.findById(id)
                              .map(UserTypeOutputDto::from)
                              .orElseThrow(() -> new UserTypeNotFoundException(id));
    }
}
