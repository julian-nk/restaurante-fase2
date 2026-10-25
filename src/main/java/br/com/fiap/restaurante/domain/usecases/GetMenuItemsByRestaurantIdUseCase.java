
package br.com.fiap.restaurante.domain.usecases;

import br.com.fiap.restaurante.domain.entities.MenuItem;
import br.com.fiap.restaurante.interfaces.IMenuItemGateway;

import java.util.List;

public class GetMenuItemsByRestaurantIdUseCase {

    private final IMenuItemGateway menuItemGateway;

    public GetMenuItemsByRestaurantIdUseCase(
            IMenuItemGateway menuItemGateway
    ) {
        this.menuItemGateway = menuItemGateway;
    }

    public List<MenuItem> execute(Long restaurantId) {
        if (restaurantId == null || restaurantId <= 0) {
            throw new IllegalArgumentException(
                    "O ID do restaurante deve ser maior que zero."
            );
        }

        return menuItemGateway.findByRestaurantId(restaurantId);
    }
}
