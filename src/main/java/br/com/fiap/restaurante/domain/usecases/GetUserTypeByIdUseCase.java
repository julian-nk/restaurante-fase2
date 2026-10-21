package br.com.fiap.restaurante.domain.usecases;

import br.com.fiap.restaurante.domain.entities.UserType;
import br.com.fiap.restaurante.interfaces.IUserTypeGateway;

import java.util.Optional;

public class GetUserTypeByIdUseCase {
    private final IUserTypeGateway userTypeGateway;

    public GetUserTypeByIdUseCase(IUserTypeGateway userTypeGateway) {
        this.userTypeGateway = userTypeGateway;
    }

    public Optional<UserType> execute(Long id) {
        return userTypeGateway.findById(id);
    }
}
