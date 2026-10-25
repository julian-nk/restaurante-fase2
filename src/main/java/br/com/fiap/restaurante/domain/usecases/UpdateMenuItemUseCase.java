
package br.com.fiap.restaurante.domain.usecases;

import br.com.fiap.restaurante.domain.entities.MenuItem;
import br.com.fiap.restaurante.domain.exceptions.ResourceNotFoundException;
import br.com.fiap.restaurante.interfaces.IMenuItemGateway;

import java.math.BigDecimal;

public class UpdateMenuItemUseCase {

    private final IMenuItemGateway menuItemGateway;

    public UpdateMenuItemUseCase(IMenuItemGateway menuItemGateway) {
        this.menuItemGateway = menuItemGateway;
    }

    public MenuItem execute(
            Long id,
            String name,
            String description,
            BigDecimal price,
            boolean dineInOnly,
            String photoUrl
    ) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "O ID do prato deve ser maior que zero."
            );
        }

        MenuItem menuItem = menuItemGateway.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Prato não encontrado com o ID: " + id
                ));

        if (name == null || name.isBlank() || name.length() > 150) {
            throw new IllegalArgumentException(
                    "O nome é obrigatório e deve ter no máximo 150 caracteres."
            );
        }

        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException(
                    "A descrição do prato é obrigatória."
            );
        }

        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "O preço deve ser maior ou igual a zero."
            );
        }

        menuItem.update(
                name.trim(),
                description.trim(),
                price,
                dineInOnly,
                photoUrl
        );

        return menuItemGateway.save(menuItem);
    }
}
