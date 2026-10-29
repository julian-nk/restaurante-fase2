package br.com.fiap.restaurante.core.dto;

import br.com.fiap.restaurante.core.domain.Restaurant;

import java.util.List;

public record RestaurantOutputDto(
        Long id,
        String name,
        String cnpj,
        String cuisineType,
        Long ownerId,
        AddressOutputDto address,
        List<OpeningHoursOutputDto> openingHours
) {

    public static RestaurantOutputDto from(Restaurant restaurant) {
        return new RestaurantOutputDto(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getCnpj(),
                restaurant.getCuisineType(),
                restaurant.getOwnerId(),
                AddressOutputDto.from(restaurant.getAddress()),
                restaurant.getOpeningHours().stream().map(OpeningHoursOutputDto::from).toList()
        );
    }
}
