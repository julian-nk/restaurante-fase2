package br.com.fiap.restaurante.core.usecase.impl;

import br.com.fiap.restaurante.core.dto.RestaurantOutputDto;
import br.com.fiap.restaurante.core.exception.RestaurantNotFoundException;
import br.com.fiap.restaurante.core.gateway.RestaurantGateway;
import br.com.fiap.restaurante.core.usecase.GetRestaurantByIdUseCase;

public class GetRestaurantByIdUseCaseImpl implements GetRestaurantByIdUseCase {

    private final RestaurantGateway restaurantGateway;

    public GetRestaurantByIdUseCaseImpl(RestaurantGateway restaurantGateway) {
        this.restaurantGateway = restaurantGateway;
    }

    @Override
    public RestaurantOutputDto execute(Long id) {
        return restaurantGateway.findById(id)
                .map(RestaurantOutputDto::from)
                .orElseThrow(() -> new RestaurantNotFoundException(id));
    }
}
