package br.com.fiap.restaurante.core.usecase.impl;

import br.com.fiap.restaurante.core.exception.RestaurantOwnerHasRestaurantsException;
import br.com.fiap.restaurante.core.exception.RestaurantOwnerNotFoundException;
import br.com.fiap.restaurante.core.gateway.RestaurantGateway;
import br.com.fiap.restaurante.core.gateway.RestaurantOwnerGateway;
import br.com.fiap.restaurante.core.usecase.DeleteRestaurantOwnerUseCase;

public class DeleteRestaurantOwnerUseCaseImpl implements DeleteRestaurantOwnerUseCase {

    private final RestaurantOwnerGateway restaurantOwnerGateway;
    private final RestaurantGateway restaurantGateway;

    public DeleteRestaurantOwnerUseCaseImpl(
            RestaurantOwnerGateway restaurantOwnerGateway,
            RestaurantGateway restaurantGateway
    ) {
        this.restaurantOwnerGateway = restaurantOwnerGateway;
        this.restaurantGateway = restaurantGateway;
    }

    @Override
    public void execute(Long id) {
        if (!restaurantOwnerGateway.existsById(id)) {
            throw new RestaurantOwnerNotFoundException(id);
        }
        if (restaurantGateway.existsByOwnerId(id)) {
            throw new RestaurantOwnerHasRestaurantsException(id);
        }
        restaurantOwnerGateway.deleteById(id);
    }
}
