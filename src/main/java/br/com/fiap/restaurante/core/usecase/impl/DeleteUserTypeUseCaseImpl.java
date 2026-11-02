package br.com.fiap.restaurante.core.usecase.impl;

import br.com.fiap.restaurante.core.exception.UserTypeNotFoundException;
import br.com.fiap.restaurante.core.gateway.UserTypeGateway;
import br.com.fiap.restaurante.core.usecase.DeleteUserTypeUseCase;

public class DeleteUserTypeUseCaseImpl implements DeleteUserTypeUseCase {

    private final UserTypeGateway userTypeGateway;

    public DeleteUserTypeUseCaseImpl(UserTypeGateway userTypeGateway) {
        this.userTypeGateway = userTypeGateway;
    }

    @Override
    public void execute(Long id) {

        if (userTypeGateway.findById(id).isEmpty()) {
            throw new UserTypeNotFoundException(id);
        }

        userTypeGateway.deleteById(id);
    }
}
