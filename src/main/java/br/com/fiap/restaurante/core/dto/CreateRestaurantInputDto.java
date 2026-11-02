package br.com.fiap.restaurante.core.dto;

import java.util.List;

public record CreateRestaurantInputDto(
        String name,
        String cnpj,
        String cuisineType,
        Long ownerId,
        AddressInputDto address,
        List<OpeningHoursInputDto> openingHours
) {
}
