package br.com.fiap.restaurante.core.dto;

import br.com.fiap.restaurante.core.domain.Restaurant;

public record RestaurantSummaryOutputDto(
        Long id,
        String name,
        String cnpj,
        String cuisineType
) {

    public static RestaurantSummaryOutputDto from(Restaurant restaurant) {
        return new RestaurantSummaryOutputDto(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getCnpj(),
                restaurant.getCuisineType()
        );
    }
}
