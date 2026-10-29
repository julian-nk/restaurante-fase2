package br.com.fiap.restaurante.core.usecase.impl;

import br.com.fiap.restaurante.core.exception.RestaurantOwnerHasRestaurantsException;
import br.com.fiap.restaurante.core.exception.RestaurantOwnerNotFoundException;
import br.com.fiap.restaurante.core.gateway.CustomerGateway;
import br.com.fiap.restaurante.core.gateway.PersonGateway;
import br.com.fiap.restaurante.core.gateway.RestaurantGateway;
import br.com.fiap.restaurante.core.gateway.RestaurantOwnerGateway;
import br.com.fiap.restaurante.core.usecase.DeleteRestaurantOwnerUseCase;

/**
 * Remove o papel de dono de restaurante, desde que a pessoa não tenha mais restaurantes. Como não pode haver
 * pessoa sem papel, se ela não for também cliente a pessoa é excluída junto, com o endereço e, em cascata, o
 * usuário de acesso.
 */
public class DeleteRestaurantOwnerUseCaseImpl implements DeleteRestaurantOwnerUseCase {

    private final RestaurantOwnerGateway restaurantOwnerGateway;
    private final RestaurantGateway restaurantGateway;
    private final CustomerGateway customerGateway;
    private final PersonGateway personGateway;

    public DeleteRestaurantOwnerUseCaseImpl(
            RestaurantOwnerGateway restaurantOwnerGateway,
            RestaurantGateway restaurantGateway,
            CustomerGateway customerGateway,
            PersonGateway personGateway
    ) {
        this.restaurantOwnerGateway = restaurantOwnerGateway;
        this.restaurantGateway = restaurantGateway;
        this.customerGateway = customerGateway;
        this.personGateway = personGateway;
    }

    @Override
    public void execute(Long id) {
        if (!restaurantOwnerGateway.existsById(id)) {
            throw new RestaurantOwnerNotFoundException(id);
        }
        if (restaurantGateway.existsByOwnerId(id)) {
            throw new RestaurantOwnerHasRestaurantsException(id);
        }

        if (customerGateway.existsById(id)) {
            restaurantOwnerGateway.deleteById(id);
        } else {
            personGateway.deleteById(id);
        }
    }
}
