package br.com.fiap.restaurante.core.usecase.impl;

import br.com.fiap.restaurante.core.dto.UserTypeOutputDto;
import br.com.fiap.restaurante.core.gateway.UserTypeGateway;
import br.com.fiap.restaurante.core.usecase.GetUserTypesUseCase;

import java.util.List;

public class GetUserTypesUseCaseImpl implements GetUserTypesUseCase {

    private final UserTypeGateway userTypeGateway;

    public GetUserTypesUseCaseImpl(UserTypeGateway userTypeGateway) {
        this.userTypeGateway = userTypeGateway;
    }

    @Override
    public List<UserTypeOutputDto> execute() {
        return userTypeGateway.findAll()
                              .stream()
                              .map(UserTypeOutputDto::from)
                              .toList();
    }
}
