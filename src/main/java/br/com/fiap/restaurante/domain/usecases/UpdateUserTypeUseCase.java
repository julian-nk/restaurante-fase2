package br.com.fiap.restaurante.domain.usecases;

import br.com.fiap.restaurante.domain.entities.UserType;
import br.com.fiap.restaurante.interfaces.IUserTypeGateway;

import java.util.Optional;

public class UpdateUserTypeUseCase {

    private final IUserTypeGateway userTypeGateway;

    public UpdateUserTypeUseCase(IUserTypeGateway userTypeGateway) {
        this.userTypeGateway = userTypeGateway;
    }

    public Optional<UserType> execute(
            Long id,
            String name,
            String code) {

        return userTypeGateway.findById(id)
                .map(userType -> {
                    userType.update(name, code);
                    return userTypeGateway.save(userType);
                });
    }
}