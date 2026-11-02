package br.com.fiap.restaurante.core.usecase.impl;

import br.com.fiap.restaurante.core.dto.MenuItemOutputDto;
import br.com.fiap.restaurante.core.gateway.MenuItemGateway;
import br.com.fiap.restaurante.core.usecase.GetMenuItemsByRestaurantIdUseCase;

import java.util.List;

public class GetMenuItemsByRestaurantIdUseCaseImpl implements GetMenuItemsByRestaurantIdUseCase {

    private final MenuItemGateway menuItemGateway;

    public GetMenuItemsByRestaurantIdUseCaseImpl(MenuItemGateway menuItemGateway) {
        this.menuItemGateway = menuItemGateway;
    }

    @Override
    public List<MenuItemOutputDto> execute(Long restaurantId) {

        return menuItemGateway.findByRestaurantId(restaurantId)
                .stream()
                .map(MenuItemOutputDto::from)
                .toList();
    }
}
