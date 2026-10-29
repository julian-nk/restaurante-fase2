package br.com.fiap.restaurante.core.usecase.impl;

import br.com.fiap.restaurante.core.exception.RestaurantNotFoundException;
import br.com.fiap.restaurante.core.gateway.RestaurantGateway;
import br.com.fiap.restaurante.core.usecase.DeleteRestaurantUseCase;

/** Remove o restaurante; no banco, os itens do cardápio são removidos em cascata. */
public class DeleteRestaurantUseCaseImpl implements DeleteRestaurantUseCase {

    private final RestaurantGateway restaurantGateway;

    public DeleteRestaurantUseCaseImpl(RestaurantGateway restaurantGateway) {
        this.restaurantGateway = restaurantGateway;
    }

    @Override
    public void execute(Long id) {
        if (!restaurantGateway.existsById(id)) {
            throw new RestaurantNotFoundException(id);
        }
        restaurantGateway.deleteById(id);
    }
}
