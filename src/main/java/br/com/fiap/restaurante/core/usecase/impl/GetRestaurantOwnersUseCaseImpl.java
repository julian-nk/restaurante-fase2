package br.com.fiap.restaurante.core.usecase.impl;

import br.com.fiap.restaurante.core.dto.RestaurantOwnerOutputDto;
import br.com.fiap.restaurante.core.gateway.RestaurantOwnerGateway;
import br.com.fiap.restaurante.core.usecase.GetRestaurantOwnersUseCase;

import java.util.List;

public class GetRestaurantOwnersUseCaseImpl implements GetRestaurantOwnersUseCase {

    private final RestaurantOwnerGateway restaurantOwnerGateway;

    public GetRestaurantOwnersUseCaseImpl(RestaurantOwnerGateway restaurantOwnerGateway) {
        this.restaurantOwnerGateway = restaurantOwnerGateway;
    }

    @Override
    public List<RestaurantOwnerOutputDto> execute() {
        return restaurantOwnerGateway.findAll().stream().map(RestaurantOwnerOutputDto::from).toList();
    }
}
