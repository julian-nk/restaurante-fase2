package br.com.fiap.restaurante.core.usecase.impl;

import br.com.fiap.restaurante.core.domain.Restaurant;
import br.com.fiap.restaurante.core.dto.CreateRestaurantInputDto;
import br.com.fiap.restaurante.core.dto.RestaurantOutputDto;
import br.com.fiap.restaurante.core.exception.CnpjAlreadyRegisteredException;
import br.com.fiap.restaurante.core.exception.RestaurantOwnerNotFoundException;
import br.com.fiap.restaurante.core.factory.RestaurantFactory;
import br.com.fiap.restaurante.core.gateway.RestaurantGateway;
import br.com.fiap.restaurante.core.gateway.RestaurantOwnerGateway;
import br.com.fiap.restaurante.core.usecase.CreateRestaurantUseCase;

public class CreateRestaurantUseCaseImpl implements CreateRestaurantUseCase {

    private final RestaurantGateway restaurantGateway;
    private final RestaurantOwnerGateway restaurantOwnerGateway;
    private final RestaurantFactory restaurantFactory;

    public CreateRestaurantUseCaseImpl(
            RestaurantGateway restaurantGateway,
            RestaurantOwnerGateway restaurantOwnerGateway,
            RestaurantFactory restaurantFactory
    ) {
        this.restaurantGateway = restaurantGateway;
        this.restaurantOwnerGateway = restaurantOwnerGateway;
        this.restaurantFactory = restaurantFactory;
    }

    @Override
    public RestaurantOutputDto execute(CreateRestaurantInputDto input) {
        Restaurant restaurant = restaurantFactory.create(input);

        if (!restaurantOwnerGateway.existsById(restaurant.getOwnerId())) {
            throw new RestaurantOwnerNotFoundException(restaurant.getOwnerId());
        }
        if (restaurantGateway.existsByCnpj(restaurant.getCnpj())) {
            throw new CnpjAlreadyRegisteredException();
        }

        return RestaurantOutputDto.from(restaurantGateway.save(restaurant));
    }
}
