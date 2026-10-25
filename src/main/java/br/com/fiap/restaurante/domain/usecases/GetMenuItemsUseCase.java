
package br.com.fiap.restaurante.domain.usecases;

import br.com.fiap.restaurante.domain.entities.MenuItem;
import br.com.fiap.restaurante.interfaces.IMenuItemGateway;

import java.util.List;

public class GetMenuItemsUseCase {

    private final IMenuItemGateway menuItemGateway;

    public GetMenuItemsUseCase(IMenuItemGateway menuItemGateway) {
        this.menuItemGateway = menuItemGateway;
    }

    public List<MenuItem> execute() {
        return menuItemGateway.findAll();
    }
}
