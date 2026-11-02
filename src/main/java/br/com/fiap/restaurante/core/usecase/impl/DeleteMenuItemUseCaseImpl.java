package br.com.fiap.restaurante.core.usecase.impl;

import br.com.fiap.restaurante.core.domain.MenuItem;
import br.com.fiap.restaurante.core.dto.RequesterDto;
import br.com.fiap.restaurante.core.exception.MenuItemNotFoundException;
import br.com.fiap.restaurante.core.gateway.MenuItemGateway;
import br.com.fiap.restaurante.core.gateway.RestaurantGateway;
import br.com.fiap.restaurante.core.usecase.DeleteMenuItemUseCase;

public class DeleteMenuItemUseCaseImpl implements DeleteMenuItemUseCase {

    private final MenuItemGateway menuItemGateway;
    private final RestaurantGateway restaurantGateway;

    public DeleteMenuItemUseCaseImpl(MenuItemGateway menuItemGateway, RestaurantGateway restaurantGateway) {
        this.menuItemGateway = menuItemGateway;
        this.restaurantGateway = restaurantGateway;
    }

    @Override
    public void execute(Long id, RequesterDto requester) {

        MenuItem menuItem = menuItemGateway.findById(id)
                                           .orElseThrow(() -> new MenuItemNotFoundException(id));
        RestaurantAccessService.findManageable(restaurantGateway, menuItem.getRestaurantId(), requester);

        menuItemGateway.deleteById(id);
    }
}
