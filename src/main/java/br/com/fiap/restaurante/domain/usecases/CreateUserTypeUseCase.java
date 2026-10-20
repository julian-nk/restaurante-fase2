package br.com.fiap.restaurante.domain.usecases;

import br.com.fiap.restaurante.domain.entities.UserType;
import br.com.fiap.restaurante.interfaces.IUserTypeGateway;

public class CreateUserTypeUseCase {
    private final IUserTypeGateway gateway;

    public CreateUserTypeUseCase(IUserTypeGateway gateway) {
        this.gateway = gateway;
    }

    public UserType execute(String name, String code) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "O nome do tipo de usuário é obrigatório."
            );
        }

        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException(
                    "O código do tipo de usuário é obrigatório."
            );
        }

        if (gateway.existsByCode(code)) {
            throw new IllegalArgumentException(
                    "Já existe um tipo de usuário com esse código."
            );
        }

        UserType userType = new UserType(name, code);

        return gateway.save(userType);
    }

}
