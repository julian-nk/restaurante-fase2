
package br.com.fiap.restaurante.dtos;

import java.math.BigDecimal;

public record MenuItemResponse(
        Long id,
        Long restaurantId,
        String name,
        String description,
        BigDecimal price,
        boolean dineInOnly,
        String photoUrl
) {}
