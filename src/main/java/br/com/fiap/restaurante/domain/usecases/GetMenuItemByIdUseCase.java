
package br.com.fiap.restaurante.domain.usecases;

import br.com.fiap.restaurante.domain.entities.MenuItem;
import br.com.fiap.restaurante.domain.exceptions.ResourceNotFoundException;
import br.com.fiap.restaurante.interfaces.IMenuItemGateway;

public class GetMenuItemByIdUseCase {

    private final IMenuItemGateway menuItemGateway;

    public GetMenuItemByIdUseCase(IMenuItemGateway menuItemGateway) {
        this.menuItemGateway = menuItemGateway;
    }

    public MenuItem execute(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "O ID do prato deve ser maior que zero."
            );
        }

        return menuItemGateway.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Prato não encontrado com o ID: " + id
                ));
    }
}
