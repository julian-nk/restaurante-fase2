package br.com.fiap.restaurante.core.factory;

import br.com.fiap.restaurante.core.domain.Address;
import br.com.fiap.restaurante.core.domain.Restaurant;
import br.com.fiap.restaurante.core.domain.RestaurantOwner;
import br.com.fiap.restaurante.core.dto.CreateRestaurantOwnerInputDto;

import java.time.OffsetDateTime;
import java.util.List;

public interface RestaurantOwnerFactory {

    RestaurantOwner create(Long personId, CreateRestaurantOwnerInputDto input);

    RestaurantOwner restore(
            Long id,
            String name,
            String email,
            String cpf,
            Address address,
            OffsetDateTime createdOn,
            String businessEmail,
            List<Restaurant> ownedRestaurants
    );
}
