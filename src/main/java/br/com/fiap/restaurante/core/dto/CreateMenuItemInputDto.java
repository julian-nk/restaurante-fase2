package br.com.fiap.restaurante.core.dto;

import java.math.BigDecimal;

public record CreateMenuItemInputDto(
        Long restaurantId,
        String name,
        String description,
        BigDecimal price,
        boolean dineInOnly,
        String photoUrl
) {
}
