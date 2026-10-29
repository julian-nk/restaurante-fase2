package br.com.fiap.restaurante.core.usecase.impl;

import br.com.fiap.restaurante.core.dto.RestaurantOwnerOutputDto;
import br.com.fiap.restaurante.core.exception.RestaurantOwnerNotFoundException;
import br.com.fiap.restaurante.core.gateway.RestaurantOwnerGateway;
import br.com.fiap.restaurante.core.usecase.GetRestaurantOwnerByIdUseCase;

public class GetRestaurantOwnerByIdUseCaseImpl implements GetRestaurantOwnerByIdUseCase {

    private final RestaurantOwnerGateway restaurantOwnerGateway;

    public GetRestaurantOwnerByIdUseCaseImpl(RestaurantOwnerGateway restaurantOwnerGateway) {
        this.restaurantOwnerGateway = restaurantOwnerGateway;
    }

    @Override
    public RestaurantOwnerOutputDto execute(Long personId) {
        return restaurantOwnerGateway.findById(personId)
                .map(RestaurantOwnerOutputDto::from)
                .orElseThrow(() -> new RestaurantOwnerNotFoundException(personId));
    }
}
