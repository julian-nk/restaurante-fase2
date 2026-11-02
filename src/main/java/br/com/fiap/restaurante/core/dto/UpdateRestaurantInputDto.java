package br.com.fiap.restaurante.core.dto;

import java.util.List;

public record UpdateRestaurantInputDto(
        Long id,
        String name,
        String cuisineType,
        Long ownerId,
        AddressInputDto address,
        List<OpeningHoursInputDto> openingHours
) {
}
