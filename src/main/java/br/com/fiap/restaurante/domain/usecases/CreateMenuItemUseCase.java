
package br.com.fiap.restaurante.domain.usecases;

import br.com.fiap.restaurante.domain.entities.MenuItem;
import br.com.fiap.restaurante.domain.exceptions.BusinessException;
import br.com.fiap.restaurante.interfaces.IMenuItemGateway;

import java.math.BigDecimal;

public class CreateMenuItemUseCase {

    private final IMenuItemGateway menuItemGateway;

    public CreateMenuItemUseCase(IMenuItemGateway menuItemGateway) {
        this.menuItemGateway = menuItemGateway;
    }

    public MenuItem execute(
            Long restaurantId,
            String name,
            String description,
            BigDecimal price,
            boolean dineInOnly,
            String photoUrl
    ) {
        validate(restaurantId, name, description, price);

        MenuItem menuItem = new MenuItem(
                restaurantId,
                name.trim(),
                description.trim(),
                price,
                dineInOnly,
                photoUrl
        );

        return menuItemGateway.save(menuItem);
    }

    private void validate(
            Long restaurantId,
            String name,
            String description,
            BigDecimal price
    ) {
        if (restaurantId == null || restaurantId <= 0) {
            throw new IllegalArgumentException(
                    "O ID do restaurante deve ser maior que zero."
            );
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "O nome do prato é obrigatório."
            );
        }

        if (name.length() > 150) {
            throw new IllegalArgumentException(
                    "O nome do prato deve ter no máximo 150 caracteres."
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
    }
}
