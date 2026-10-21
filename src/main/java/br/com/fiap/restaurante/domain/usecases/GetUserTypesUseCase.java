package br.com.fiap.restaurante.domain.usecases;

import br.com.fiap.restaurante.domain.entities.UserType;
import br.com.fiap.restaurante.interfaces.IUserTypeGateway;

import java.util.List;

public class GetUserTypesUseCase {
    private final IUserTypeGateway userTypeGateway;

    public GetUserTypesUseCase(IUserTypeGateway userTypeGateway) {
        this.userTypeGateway = userTypeGateway;
    }

    public List<UserType> execute() {
        return userTypeGateway.findAll();
    }
}
