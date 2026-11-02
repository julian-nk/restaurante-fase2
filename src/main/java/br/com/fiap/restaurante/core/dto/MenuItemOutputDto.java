package br.com.fiap.restaurante.core.dto;

import br.com.fiap.restaurante.core.domain.MenuItem;

import java.math.BigDecimal;

public record MenuItemOutputDto(
        Long id,
        Long restaurantId,
        String name,
        String description,
        BigDecimal price,
        boolean dineInOnly,
        String photoUrl
) {

    public static MenuItemOutputDto from(MenuItem menuItem) {
        return new MenuItemOutputDto(
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
