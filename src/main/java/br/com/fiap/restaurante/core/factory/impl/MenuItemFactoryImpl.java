package br.com.fiap.restaurante.core.factory.impl;

import br.com.fiap.restaurante.core.domain.MenuItem;
import br.com.fiap.restaurante.core.dto.CreateMenuItemInputDto;
import br.com.fiap.restaurante.core.factory.MenuItemFactory;

import java.math.BigDecimal;

public class MenuItemFactoryImpl implements MenuItemFactory {

    @Override
    public MenuItem create(CreateMenuItemInputDto input) {
        return MenuItem.builder()
                .restaurantId(input.restaurantId())
                .name(input.name())
                .description(input.description())
                .price(input.price())
                .dineInOnly(input.dineInOnly())
                .photoUrl(input.photoUrl())
                .build();
    }

    @Override
    public MenuItem restore(
            Long id,
            Long restaurantId,
            String name,
            String description,
            BigDecimal price,
            boolean dineInOnly,
            String photoUrl
    ) {
        return MenuItem.builder()
                .id(id)
                .restaurantId(restaurantId)
                .name(name)
                .description(description)
                .price(price)
                .dineInOnly(dineInOnly)
                .photoUrl(photoUrl)
                .build();
    }
}
