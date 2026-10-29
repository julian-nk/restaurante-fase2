package br.com.fiap.restaurante.core.usecase.impl;

import br.com.fiap.restaurante.core.domain.MenuItem;
import br.com.fiap.restaurante.core.dto.CreateMenuItemInputDto;
import br.com.fiap.restaurante.core.dto.MenuItemOutputDto;
import br.com.fiap.restaurante.core.exception.RestaurantNotFoundException;
import br.com.fiap.restaurante.core.factory.MenuItemFactory;
import br.com.fiap.restaurante.core.gateway.MenuItemGateway;
import br.com.fiap.restaurante.core.gateway.RestaurantGateway;
import br.com.fiap.restaurante.core.usecase.CreateMenuItemUseCase;

public class CreateMenuItemUseCaseImpl implements CreateMenuItemUseCase {

    private final MenuItemGateway menuItemGateway;
    private final RestaurantGateway restaurantGateway;
    private final MenuItemFactory menuItemFactory;

    public CreateMenuItemUseCaseImpl(
            MenuItemGateway menuItemGateway,
            RestaurantGateway restaurantGateway,
            MenuItemFactory menuItemFactory
    ) {
        this.menuItemGateway = menuItemGateway;
        this.restaurantGateway = restaurantGateway;
        this.menuItemFactory = menuItemFactory;
    }

    @Override
    public MenuItemOutputDto execute(CreateMenuItemInputDto input) {
        MenuItem menuItem = menuItemFactory.create(input);

        if (!restaurantGateway.existsById(menuItem.getRestaurantId())) {
            throw new RestaurantNotFoundException(menuItem.getRestaurantId());
        }

        return MenuItemOutputDto.from(menuItemGateway.save(menuItem));
    }
}
