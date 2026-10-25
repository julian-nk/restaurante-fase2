
package br.com.fiap.restaurante.domain.usecases;

import br.com.fiap.restaurante.domain.exceptions.ResourceNotFoundException;
import br.com.fiap.restaurante.interfaces.IMenuItemGateway;

public class DeleteMenuItemUseCase {

    private final IMenuItemGateway menuItemGateway;

    public DeleteMenuItemUseCase(IMenuItemGateway menuItemGateway) {
        this.menuItemGateway = menuItemGateway;
    }

    public void execute(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "O ID do prato deve ser maior que zero."
            );
        }

        menuItemGateway.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Prato não encontrado com o ID: " + id
                ));

        menuItemGateway.deleteById(id);
    }
}
