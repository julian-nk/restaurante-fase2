package br.com.fiap.restaurante.core.usecase.impl;

import br.com.fiap.restaurante.core.domain.MenuItem;
import br.com.fiap.restaurante.core.dto.MenuItemOutputDto;
import br.com.fiap.restaurante.core.dto.UpdateMenuItemInputDto;
import br.com.fiap.restaurante.core.exception.MenuItemNotFoundException;
import br.com.fiap.restaurante.core.gateway.MenuItemGateway;
import br.com.fiap.restaurante.core.usecase.UpdateMenuItemUseCase;

public class UpdateMenuItemUseCaseImpl implements UpdateMenuItemUseCase {

    private final MenuItemGateway menuItemGateway;

    public UpdateMenuItemUseCaseImpl(MenuItemGateway menuItemGateway) {
        this.menuItemGateway = menuItemGateway;
    }

    @Override
    public MenuItemOutputDto execute(UpdateMenuItemInputDto input) {

        MenuItem menuItem = menuItemGateway.findById(input.id())
                                           .orElseThrow(() -> new MenuItemNotFoundException(input.id()));

        menuItem.update(
                input.name(),
                input.description(),
                input.price(),
                input.dineInOnly(),
                input.photoUrl()
        );

        return MenuItemOutputDto.from(menuItemGateway.save(menuItem));
    }
}
