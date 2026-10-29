package br.com.fiap.restaurante.core.usecase.impl;

import br.com.fiap.restaurante.core.exception.PersonNotFoundException;
import br.com.fiap.restaurante.core.exception.RestaurantOwnerHasRestaurantsException;
import br.com.fiap.restaurante.core.gateway.PersonGateway;
import br.com.fiap.restaurante.core.gateway.RestaurantGateway;
import br.com.fiap.restaurante.core.usecase.DeletePersonUseCase;

/**
 * Exclui a pessoa e o endereço dela. Os papéis (cliente e dono de restaurante) e o usuário de acesso são excluídos
 * junto. Quem ainda é dono de restaurantes não pode ser excluído: os restaurantes precisam ser transferidos ou
 * removidos antes.
 */
public class DeletePersonUseCaseImpl implements DeletePersonUseCase {

    private final PersonGateway personGateway;
    private final RestaurantGateway restaurantGateway;

    public DeletePersonUseCaseImpl(PersonGateway personGateway, RestaurantGateway restaurantGateway) {
        this.personGateway = personGateway;
        this.restaurantGateway = restaurantGateway;
    }

    @Override
    public void execute(Long id) {
        if (!personGateway.existsById(id)) {
            throw new PersonNotFoundException(id);
        }
        if (restaurantGateway.existsByOwnerId(id)) {
            throw new RestaurantOwnerHasRestaurantsException(id);
        }
        personGateway.deleteById(id);
    }
}
