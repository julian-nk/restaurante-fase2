package br.com.fiap.restaurante.core.usecase.impl;

import br.com.fiap.restaurante.core.exception.MenuItemNotFoundException;
import br.com.fiap.restaurante.core.gateway.MenuItemGateway;
import br.com.fiap.restaurante.core.usecase.DeleteMenuItemUseCase;

public class DeleteMenuItemUseCaseImpl implements DeleteMenuItemUseCase {

    private final MenuItemGateway menuItemGateway;

    public DeleteMenuItemUseCaseImpl(MenuItemGateway menuItemGateway) {
        this.menuItemGateway = menuItemGateway;
    }

    @Override
    public void execute(Long id) {

        if (menuItemGateway.findById(id).isEmpty()) {
            throw new MenuItemNotFoundException(id);
        }

        menuItemGateway.deleteById(id);
    }
}
