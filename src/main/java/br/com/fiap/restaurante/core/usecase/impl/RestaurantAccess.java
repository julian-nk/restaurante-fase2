package br.com.fiap.restaurante.core.usecase.impl;

import br.com.fiap.restaurante.core.domain.Restaurant;
import br.com.fiap.restaurante.core.dto.RequesterDto;
import br.com.fiap.restaurante.core.exception.NotRestaurantOwnerException;
import br.com.fiap.restaurante.core.exception.RestaurantNotFoundException;
import br.com.fiap.restaurante.core.gateway.RestaurantGateway;

final class RestaurantAccess {

    private RestaurantAccess() {
    }

    static void requireCanManage(Restaurant restaurant, RequesterDto requester) {
        if (!requester.admin() && !restaurant.isOwnedBy(requester.personId())) {
            throw new NotRestaurantOwnerException();
        }
    }

    /**
     * Busca o restaurante e confere se quem chama pode mexer nele.
     */
    static Restaurant findManageable(RestaurantGateway restaurantGateway, Long restaurantId, RequesterDto requester) {
        Restaurant restaurant = restaurantGateway.findById(restaurantId)
                                                 .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
        requireCanManage(restaurant, requester);
        return restaurant;
    }
}
