package br.com.fiap.restaurante.domain.usecases;

import br.com.fiap.restaurante.interfaces.IUserTypeGateway;

public class DeleteUserTypeUseCase {
    private final IUserTypeGateway userTypeGateway;

    public DeleteUserTypeUseCase(IUserTypeGateway userTypeGateway) {
        this.userTypeGateway = userTypeGateway;
    }

    public boolean execute(Long id) {
        return userTypeGateway.findById(id)
                .map(userType -> {
                    userTypeGateway.deleteById(id);
                    return true;
                })
                .orElse(false);
    }
}
