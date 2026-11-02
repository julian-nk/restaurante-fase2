package br.com.fiap.restaurante.core.usecase.impl;

import br.com.fiap.restaurante.core.domain.UserType;
import br.com.fiap.restaurante.core.exception.RestaurantOwnerHasRestaurantsException;
import br.com.fiap.restaurante.core.exception.RestaurantOwnerNotFoundException;
import br.com.fiap.restaurante.core.gateway.RestaurantGateway;
import br.com.fiap.restaurante.core.gateway.RestaurantOwnerGateway;
import br.com.fiap.restaurante.core.usecase.DeleteRestaurantOwnerUseCase;

public class DeleteRestaurantOwnerUseCaseImpl implements DeleteRestaurantOwnerUseCase {

    private final RestaurantOwnerGateway restaurantOwnerGateway;
    private final RestaurantGateway restaurantGateway;
    private final PersonAppUserService personAppUserService;

    /** Remove o papel de dono, e o usuário da pessoa perde o tipo {@code restaurant_owner}. */
    public DeleteRestaurantOwnerUseCaseImpl(
            RestaurantOwnerGateway restaurantOwnerGateway,
            RestaurantGateway restaurantGateway,
            PersonAppUserService personAppUserService
    ) {
        this.restaurantOwnerGateway = restaurantOwnerGateway;
        this.restaurantGateway = restaurantGateway;
        this.personAppUserService = personAppUserService;
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
        personAppUserService.removeUserType(id, UserType.RESTAURANT_OWNER_CODE);
    }
}
