package br.com.fiap.restaurante.core.usecase.impl;

import br.com.fiap.restaurante.core.dto.MenuItemOutputDto;
import br.com.fiap.restaurante.core.exception.MenuItemNotFoundException;
import br.com.fiap.restaurante.core.gateway.MenuItemGateway;
import br.com.fiap.restaurante.core.usecase.GetMenuItemByIdUseCase;

public class GetMenuItemByIdUseCaseImpl implements GetMenuItemByIdUseCase {

    private final MenuItemGateway menuItemGateway;

    public GetMenuItemByIdUseCaseImpl(MenuItemGateway menuItemGateway) {
        this.menuItemGateway = menuItemGateway;
    }

    @Override
    public MenuItemOutputDto execute(Long id) {

        return menuItemGateway.findById(id)
                .map(MenuItemOutputDto::from)
                .orElseThrow(() -> new MenuItemNotFoundException(id));
    }
}
