package br.com.fiap.restaurante.infra.database.mapper;

import br.com.fiap.restaurante.core.domain.MenuItem;
import br.com.fiap.restaurante.core.factory.MenuItemFactory;
import br.com.fiap.restaurante.infra.database.jpa.entity.MenuItemEntity;
import org.springframework.stereotype.Component;

@Component
public class MenuItemMapper {

    private final MenuItemFactory menuItemFactory;

    public MenuItemMapper(MenuItemFactory menuItemFactory) {
        this.menuItemFactory = menuItemFactory;
    }

    public MenuItem map(MenuItemEntity entity) {
        return menuItemFactory.restore(
                entity.getId(),
                entity.getRestaurantId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.isDineInOnly(),
                entity.getPhotoUrl()
        );
    }

    public MenuItemEntity map(MenuItem menuItem) {
        return new MenuItemEntity(
                menuItem.getId(),
                menuItem.getRestaurantId(),
                menuItem.getName(),
                menuItem.getDescription(),
                menuItem.getPrice(),
                menuItem.isDineInOnly(),
                menuItem.getPhotoUrl()
        );
    }
}
