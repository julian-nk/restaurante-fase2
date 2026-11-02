package br.com.fiap.restaurante.core.usecase.impl;

import br.com.fiap.restaurante.core.dto.MenuItemOutputDto;
import br.com.fiap.restaurante.core.gateway.MenuItemGateway;
import br.com.fiap.restaurante.core.usecase.GetMenuItemsUseCase;

import java.util.List;

public class GetMenuItemsUseCaseImpl implements GetMenuItemsUseCase {

    private final MenuItemGateway menuItemGateway;

    public GetMenuItemsUseCaseImpl(MenuItemGateway menuItemGateway) {
        this.menuItemGateway = menuItemGateway;
    }

    @Override
    public List<MenuItemOutputDto> execute() {
        return menuItemGateway.findAll()
                .stream()
                .map(MenuItemOutputDto::from)
                .toList();
    }
}
