package br.com.fiap.restaurante.core.usecase.impl;

import br.com.fiap.restaurante.core.dto.RestaurantOutputDto;
import br.com.fiap.restaurante.core.gateway.RestaurantGateway;
import br.com.fiap.restaurante.core.usecase.GetRestaurantsUseCase;

import java.util.List;

public class GetRestaurantsUseCaseImpl implements GetRestaurantsUseCase {

    private final RestaurantGateway restaurantGateway;

    public GetRestaurantsUseCaseImpl(RestaurantGateway restaurantGateway) {
        this.restaurantGateway = restaurantGateway;
    }

    @Override
    public List<RestaurantOutputDto> execute() {
        return restaurantGateway.findAll().stream().map(RestaurantOutputDto::from).toList();
    }
}
