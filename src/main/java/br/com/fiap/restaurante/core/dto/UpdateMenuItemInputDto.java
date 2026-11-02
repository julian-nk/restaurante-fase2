package br.com.fiap.restaurante.core.dto;

import java.math.BigDecimal;

public record UpdateMenuItemInputDto(
        Long id,
        String name,
        String description,
        BigDecimal price,
        boolean dineInOnly,
        String photoUrl
) {
}
